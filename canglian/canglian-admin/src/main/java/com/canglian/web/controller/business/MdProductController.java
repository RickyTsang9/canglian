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
import com.canglian.business.domain.MdProduct;
import com.canglian.business.service.IMdProductService;

/**
 * 商品档案 信息操作处理
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/product")
public class MdProductController extends BaseController
{
    @Autowired
    private IMdProductService mdProductService;

    /**
     * 获取商品档案列表
     */
    @PreAuthorize("@ss.hasPermi('business:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(MdProduct mdProduct)
    {
        startPage();
        List<MdProduct> list = mdProductService.selectMdProductList(mdProduct);
        return getDataTable(list);
    }

    /**
     * 导入商品档案
     *
     * @param file 导入文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     * @throws Exception 导入异常
     */
    @Log(title = "商品档案", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('business:product:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<MdProduct> excelUtil = new ExcelUtil<MdProduct>(MdProduct.class);
        List<MdProduct> productList = excelUtil.importExcel(file.getInputStream());
        String message = mdProductService.importMdProduct(productList, updateSupport, getUsername());
        return success(message);
    }

    /**
     * 下载商品导入模板
     *
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<MdProduct> excelUtil = new ExcelUtil<MdProduct>(MdProduct.class);
        excelUtil.importTemplateExcel(response, "商品数据");
    }

    /**
     * 根据商品档案编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:product:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable Long productId)
    {
        return success(mdProductService.selectMdProductById(productId));
    }

    /**
     * 新增商品档案
     */
    @PreAuthorize("@ss.hasPermi('business:product:add')")
    @Log(title = "商品档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MdProduct mdProduct)
    {
        mdProduct.setCreateBy(getUsername());
        return toAjax(mdProductService.insertMdProduct(mdProduct));
    }

    /**
     * 修改商品档案
     */
    @PreAuthorize("@ss.hasPermi('business:product:edit')")
    @Log(title = "商品档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MdProduct mdProduct)
    {
        mdProduct.setUpdateBy(getUsername());
        return toAjax(mdProductService.updateMdProduct(mdProduct));
    }

    /**
     * 删除商品档案
     */
    @PreAuthorize("@ss.hasPermi('business:product:remove')")
    @Log(title = "商品档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(mdProductService.deleteMdProductByIds(productIds));
    }
}

