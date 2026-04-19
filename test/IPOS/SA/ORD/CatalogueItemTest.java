package IPOS.SA.ORD;

import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.CAT.Service.catalogueService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CatalogueItemTest {

    // testing that paracetamol is created correctly with all the right values
    @Test
    public void testMakingACatalogueItem() {
        CatalogueItem item = new CatalogueItem("100 00001", "Paracetamol", "box", "Caps", 200, 0.10, 43, 300);
        assertEquals("100 00001", item.getItemId());
        assertEquals("Paracetamol", item.getDescription());
        assertEquals("box", item.getPackageType());
        assertEquals("Caps", item.getUnit());
        assertEquals(200, item.getUnitsInPack());
        assertEquals(0.10, item.getPackageCost());
        assertEquals(43, item.getAvailabilityPacks());
        assertEquals(300, item.getStockLimitPacks());
    }

    // testing that I can update the description of aspirin
    @Test
    public void testChangingDescription() {
        CatalogueItem item = new CatalogueItem("100 00002", "Aspirin", "box", "Caps", 200, 0.50, 12413, 500);
        item.setDescription("Aspirin 500mg");
        assertEquals("Aspirin 500mg", item.getDescription());
    }

    // testing that a new item can be saved to the database
    @Test
    public void testSavingNewItem() throws Exception {
        catalogueService service = new catalogueService();
        CatalogueItem item = new CatalogueItem("TEST01", "TestMedicine", "box", "Caps", 10, 1.99, 20, 5);
        boolean saved = service.saveItem(item);
        assertTrue(saved);
    }

    // testing that I can load paracetamol from the database
    @Test
    public void testLoadingAnItem() throws Exception {
        catalogueService service = new catalogueService();
        CatalogueItem item = service.loadItem("100 00001");
        assertNotNull(item);
        assertEquals("100 00001", item.getItemId());
    }

    // testing that loading something that doesnt exist returns null
    @Test
    public void testLoadingItemThatDoesntExist() throws Exception {
        catalogueService service = new catalogueService();
        CatalogueItem item = service.loadItem("XYZ999");
        assertNull(item);
    }

    // testing that I can deactivate an item
    @Test
    public void testDeactivatingAnItem() throws Exception {
        catalogueService service = new catalogueService();
        boolean deactivated = service.deactivateItem("TEST01");
        assertTrue(deactivated);
    }

    // testing that the item is no longer active after deactivating it
    @Test
    public void testItemShouldNotBeActiveAfterDeactivating() throws Exception {
        catalogueService service = new catalogueService();
        boolean isActive = service.isItemActive("TEST01");
        assertFalse(isActive);
    }

    // testing that I can reactivate an item after it was deactivated
    @Test
    public void testReactivatingAnItem() throws Exception {
        catalogueService service = new catalogueService();
        boolean reactivated = service.reactivateItem("TEST01");
        assertTrue(reactivated);
    }
}