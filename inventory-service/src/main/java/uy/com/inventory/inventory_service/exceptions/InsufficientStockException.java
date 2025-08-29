package uy.com.inventory.inventory_service.exceptions;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String m){super(m);}
}
