package com.canglian.business;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import com.canglian.business.domain.FinCostAccount;
import com.canglian.business.domain.FinCostLayer;
import com.canglian.business.domain.FinCostLog;
import com.canglian.common.exception.ServiceException;
import com.canglian.web.controller.business.FinCostAccountController;
import com.canglian.web.controller.business.FinCostLayerController;
import com.canglian.web.controller.business.FinCostLogController;

/**
 * 成本账簿防手工篡改只读防护测试 (P4-5)
 */
class CostReadOnlyControllerGuardTest
{
    @Test
    void shouldBlockManualMutationsOnCostControllers()
    {
        FinCostAccountController accountController = new FinCostAccountController();
        assertThrows(ServiceException.class, () -> accountController.add(new FinCostAccount()));
        assertThrows(ServiceException.class, () -> accountController.edit(new FinCostAccount()));
        assertThrows(ServiceException.class, () -> accountController.remove(new Long[] { 1L }));

        FinCostLayerController layerController = new FinCostLayerController();
        assertThrows(ServiceException.class, () -> layerController.add(new FinCostLayer()));
        assertThrows(ServiceException.class, () -> layerController.edit(new FinCostLayer()));
        assertThrows(ServiceException.class, () -> layerController.remove(new Long[] { 1L }));

        FinCostLogController logController = new FinCostLogController();
        assertThrows(ServiceException.class, () -> logController.add(new FinCostLog()));
        assertThrows(ServiceException.class, () -> logController.edit(new FinCostLog()));
        assertThrows(ServiceException.class, () -> logController.remove(new Long[] { 1L }));
    }
}
