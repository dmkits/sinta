/*!!!!!!!------------------------UPDATES 2016-11-23------------------------!!!!!!!!!*/

ALTER TABLE WRH_PINV_PRODUCTS ADD COLUMN SALE_PRICE DECIMAL(10,2);
UPDATE WRH_PINV_PRODUCTS SET SALE_PRICE=SEL_PRICE;
ALTER TABLE WRH_PINV_PRODUCTS ALTER COLUMN SALE_PRICE NOT NULL;
ALTER TABLE WRH_PINV_PRODUCTS DROP COLUMN SEL_PRICE;

SELECT * FROM APP.WRH_PINV_PRODUCTS;

UPDATE CHANGE_LOG SET CHANGE_VAL ='ALTER TABLE WRH_PINV_PRODUCTS ADD COLUMN SALE_PRICE DECIMAL(10,2)' WHERE ID='wrh_pinv_products_21';
UPDATE CHANGE_LOG SET CHANGE_VAL ='ALTER TABLE WRH_PINV_PRODUCTS ALTER COLUMN SALE_PRICE NOT NULL' WHERE ID='wrh_pinv_products_22';
