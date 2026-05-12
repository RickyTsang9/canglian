package com.canglian.web.controller.business;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.common.utils.poi.ExcelUtil;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.service.IMdCustomerService;

/**
 * 客户档案 信息操作处理
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/customer")
public class MdCustomerController extends BaseController
{
    @Autowired
    private IMdCustomerService mdCustomerService;

    /**
     * 获取客户档案列表
     */
    @PreAuthorize("@ss.hasPermi('business:customer:list')")
    @GetMapping("/list")
    public TableDataInfo list(MdCustomer mdCustomer)
    {
        startPage();
        List<MdCustomer> list = mdCustomerService.selectMdCustomerList(mdCustomer);
        return getDataTable(list);
    }

    /**
     * 导入客户档案
     *
     * @param file 导入文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     * @throws Exception 导入异常
     */
    @Log(title = "客户档案", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('business:customer:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<MdCustomer> excelUtil = new ExcelUtil<MdCustomer>(MdCustomer.class);
        List<MdCustomer> customerList = excelUtil.importExcel(file.getInputStream());
        String message = mdCustomerService.importMdCustomer(customerList, updateSupport, getUsername());
        return success(message);
    }

    /**
     * 下载客户导入模板
     *
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<MdCustomer> excelUtil = new ExcelUtil<MdCustomer>(MdCustomer.class);
        excelUtil.importTemplateExcel(response, "客户数据");
    }

    /**
     * 根据客户档案编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:customer:query')")
    @GetMapping(value = "/{customerId}")
    public AjaxResult getInfo(@PathVariable Long customerId)
    {
        return success(mdCustomerService.selectMdCustomerById(customerId));
    }

    /**
     * 新增客户档案
     */
    @PreAuthorize("@ss.hasPermi('business:customer:add')")
    @Log(title = "客户档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MdCustomer mdCustomer)
    {
        mdCustomer.setCreateBy(getUsername());
        return toAjax(mdCustomerService.insertMdCustomer(mdCustomer));
    }

    /**
     * 修改客户档案
     */
    @PreAuthorize("@ss.hasPermi('business:customer:edit')")
    @Log(title = "客户档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MdCustomer mdCustomer)
    {
        mdCustomer.setUpdateBy(getUsername());
        return toAjax(mdCustomerService.updateMdCustomer(mdCustomer));
    }

    /**
     * 删除客户档案
     */
    @PreAuthorize("@ss.hasPermi('business:customer:remove')")
    @Log(title = "客户档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds)
    {
        return toAjax(mdCustomerService.deleteMdCustomerByIds(customerIds));
    }
}

