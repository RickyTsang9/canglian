package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.canglian.framework.config.ProdSecurityStartupValidator;

/**
 * 生产安全基线启动校验单元测试 (P4-1)
 */
class ProdSecurityStartupValidatorTest
{
    @Test
    void shouldRejectDefaultWeakSecretsInProd()
    {
        List<String> violations = ProdSecurityStartupValidator.validateProductionSecurity(
            ProdSecurityStartupValidator.DEFAULT_WEAK_JWT_SECRET,
            "123456",
            "123456",
            true,
            "123456",
            "",
            true);
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 5, "应检出 JWT、MySQL、Redis、Druid 弱密码/空白名单及 Swagger 违规");
    }

    @Test
    void shouldPassWhenAllProdSecretsAreHardened()
    {
        List<String> violations = ProdSecurityStartupValidator.validateProductionSecurity(
            "CanglianProdSecretKey_2026_9f8e7d6c5b4a3210_RandomStrong!",
            "MySql#Prod2026!Strong",
            "Redis#Prod2026!Strong",
            false,
            null,
            "127.0.0.1",
            false);
        assertTrue(violations.isEmpty(), "合规生产配置应全部通过校验，当前违规：" + violations);
    }

    @Test
    void shouldRejectShortJwtSecretEvenIfNotDefault()
    {
        List<String> violations = ProdSecurityStartupValidator.validateProductionSecurity(
            "short_secret_key",
            "MySql#Prod2026!Strong",
            "Redis#Prod2026!Strong",
            false,
            null,
            "127.0.0.1",
            false);
        assertTrue(violations.stream().anyMatch(v -> v.contains("JWT_SECRET")));
    }
}
