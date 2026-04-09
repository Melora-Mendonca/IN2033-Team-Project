package IPOS.SA.Comms.Interfaces;

import IPOS.SA.CAT.Model.CatalogueItem;

import java.util.List;


/**
 * Defines inventory operations such as viewing the catalogue, reserving items, and deducting stock.
 */
public interface IInventoryService {
    /**
     * Returns the current catalogue of items available for ordering.
     *
     * @return list of catalogue items
     */
    public List<CatalogueItem> getCatalogue();

    /**
     * Deducts stock for a selected item.
     *
     * @param itemID identifier of the reservation being finalized
     * @param quantity integer value of the number of stock to reduce by
     * @return true if stock has been deducted successfully, False otherwise
     */
    public boolean deductStock(String itemID, int quantity);
}
