package com.canglian.framework.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import com.canglian.common.utils.StringUtils;

/**
 * 生产环境安全基线启动校验器
 * 激活 prod profile 时，强制校验 JWT 密钥、数据库/Redis 密码与 Druid 控制台配置，防止带默认弱密钥上线。
 *
 * @author canglian
 */
@Component
public class ProdSecurityStartupValidator implements ApplicationRunner
{
    private static final Logger log = LoggerFactory.getLogger(ProdSecurityStartupValidator.class);

    public static final String DEFAULT_WEAK_JWT_SECRET = "abcdefghijklmnopqrstuvwxyz";

    public static final int MIN_JWT_SECRET_BYTES = 32;

    @Autowired
    private Environment environment;

    @Override
    public void run(ApplicationArguments args)
    {
        if (!environment.acceptsProfiles(Profiles.of("prod")))
        {
            return;
        }
        String jwtSecret = environment.getProperty("token.secret");
        String mysqlPassword = environment.getProperty("spring.datasource.druid.master.password");
        String redisPassword = environment.getProperty("spring.redis.password");
        boolean druidViewEnabled = Boolean.parseBoolean(
            environment.getProperty("spring.datasource.druid.statViewServlet.enabled", "false"));
        String druidPassword = environment.getProperty("spring.datasource.druid.statViewServlet.login-password");
        String druidAllow = environment.getProperty("spring.datasource.druid.statViewServlet.allow");
        boolean swaggerEnabled = Boolean.parseBoolean(environment.getProperty("swagger.enabled", "false"));

        List<String> violations = validateProductionSecurity(
            jwtSecret, mysqlPassword, redisPassword, druidViewEnabled, druidPassword, druidAllow, swaggerEnabled);
        if (!violations.isEmpty())
        {
            String message = "生产环境安全基线校验未通过，已阻止应用启动：\n - " + String.join("\n - ", violations);
            log.error(message);
            throw new IllegalStateException(message);
        }
        log.info("生产环境安全基线启动校验通过。");
    }

    /**
     * 校验生产安全基线配置项，返回违规列表（空列表表示通过）
     */
    public static List<String> validateProductionSecurity(
        String jwtSecret,
        String mysqlPassword,
        String redisPassword,
        boolean druidViewEnabled,
        String druidPassword,
        String druidAllow,
        boolean swaggerEnabled)
    {
        List<String> violations = new ArrayList<String>();
        if (StringUtils.isEmpty(jwtSecret)
            || DEFAULT_WEAK_JWT_SECRET.equals(jwtSecret)
            || jwtSecret.getBytes(StandardCharsets.UTF_8).length < MIN_JWT_SECRET_BYTES)
        {
            violations.add("token.secret(JWT_SECRET) 必须通过环境变量设置为长度 >= 32 字节的非默认强随机密钥");
        }
        if (isWeakPassword(mysqlPassword))
        {
            violations.add("MYSQL_PASSWORD 不能使用空密码或默认弱密码(123456/root/admin)");
        }
        if (isWeakPassword(redisPassword))
        {
            violations.add("REDIS_PASSWORD 不能使用空密码或默认弱密码(123456/root/admin)");
        }
        if (druidViewEnabled)
        {
            if (isWeakPassword(druidPassword))
            {
                violations.add("生产环境开启 Druid 控制台时 DRUID_PASSWORD 不能为弱密码");
            }
            if (StringUtils.isEmpty(druidAllow))
            {
                violations.add("生产环境开启 Druid 控制台时 DRUID_ALLOW 必须配置允许访问的 IP 白名单");
            }
        }
        if (swaggerEnabled)
        {
            violations.add("生产环境禁止开启 Swagger 接口文档(swagger.enabled=true)");
        }
        return violations;
    }

    private static boolean isWeakPassword(String password)
    {
        if (StringUtils.isEmpty(password))
        {
            return true;
        }
        String trimmed = password.trim();
        return "123456".equals(trimmed) || "root".equalsIgnoreCase(trimmed)
            || "admin".equalsIgnoreCase(trimmed) || "password".equalsIgnoreCase(trimmed)
            || trimmed.length() < 8;
    }
}
