package IPOS.SA.Comms.Interfaces;

import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.CAT.Service.catalogueService;

import java.util.List;

public class InventoryServiceImpl implements IInventoryService {
    private final catalogueService catService;

    /**
     * Creates an inventory service with its required catalogue dependency.
     */
    public InventoryServiceImpl() {

        this.catService = new catalogueService();
    }

    /**
     * Retrieves the current catalogue of available items.
     *
     * @return list of catalogue items available for purchase
     */
    public List<CatalogueItem> getCatalogue() {

        try {
            return catService.getAllActiveItems();
        } catch (Exception e) {
            System.err.println("Error accessing catalogue: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deducts stock associated with a reservation once it is confirmed.
     *
     * @param itemID identifier of the item to deduct
     * @param quantity integer value of the number of stock to reduce by
     * @return confirmation reference of the deduction
     */
    public boolean deductStock(String itemID, int quantity) {

        try {
            // Deducts x unit of stock
            return catService.UpdateCatalogue(itemID, quantity);
        } catch (Exception e) {
            System.err.println("Error deducting stock: " + e.getMessage());
            return false;
        }
    }
}
