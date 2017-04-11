package org.kuali.kfs.module.cam.batch.service.impl;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.util.Collections;
import java.util.List;

public class AssetBarcodeInventoryLoadServiceImplTest {
    private AssetService assetService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;

    private AssetBarcodeInventoryLoadServiceImpl assetBarcodeInventoryLoadService;

    @Before
    public void setUp() {
        assetBarcodeInventoryLoadService = new AssetBarcodeInventoryLoadServiceImpl();
        assetService = EasyMock.createMock(AssetService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        dateTimeService = EasyMock.createMock(DateTimeService.class);

        assetBarcodeInventoryLoadService.setAssetService(assetService);
        assetBarcodeInventoryLoadService.setBusinessObjectService(businessObjectService);
        assetBarcodeInventoryLoadService.setDateTimeService(dateTimeService);
    }

    @Test
    public void testUpdateAssetInformationTrue() {
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail = new BarcodeInventoryErrorDetail();
        barcodeInventoryErrorDetail.setAssetTagNumber("IU025557");

        EasyMock.expect(assetService.findActiveAssetsMatchingTagNumber("IU025557")).andReturn(getAssets());
        EasyMock.expect(businessObjectService.save(EasyMock.anyObject(Asset.class))).andReturn(new Asset());

        EasyMock.replay(assetService, businessObjectService, dateTimeService);

        assetBarcodeInventoryLoadService.updateAssetInformation(barcodeInventoryErrorDetail,true);

        EasyMock.verify(assetService, businessObjectService, dateTimeService);
    }

    @Test
    public void testUpdateAssetInformationFalse() {
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail = new BarcodeInventoryErrorDetail();
        barcodeInventoryErrorDetail.setAssetTagNumber("IU025557");

        EasyMock.expect(assetService.findActiveAssetsMatchingTagNumber("IU025557")).andReturn(getAssets());
        EasyMock.expect(businessObjectService.save(EasyMock.anyObject(Asset.class))).andReturn(new Asset());

        java.util.Date jud = new java.util.Date();
        java.sql.Date jsd = new java.sql.Date(jud.getTime());
        EasyMock.expect(dateTimeService.getCurrentSqlDate()).andReturn(jsd);

        EasyMock.replay(assetService, businessObjectService, dateTimeService);

        assetBarcodeInventoryLoadService.updateAssetInformation(barcodeInventoryErrorDetail,false);

        EasyMock.verify(assetService, businessObjectService, dateTimeService);
    }

    private List<Asset> getAssets() {
        Asset asset = new Asset();

        return Collections.singletonList(asset);
    }
}