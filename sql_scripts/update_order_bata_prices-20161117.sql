ALTER TABLE APP.WRH_ORDER_BATA_DETAILS ADD COLUMN PRICE_TMP DECIMAL(12,4);
UPDATE APP.WRH_ORDER_BATA_DETAILS SET PRICE_TMP=PRICE;
ALTER TABLE APP.WRH_ORDER_BATA_DETAILS DROP COLUMN PRICE;
ALTER TABLE APP.WRH_ORDER_BATA_DETAILS ADD COLUMN PRICE DECIMAL(12,4);
UPDATE APP.WRH_ORDER_BATA_DETAILS SET PRICE=PRICE_TMP;
ALTER TABLE APP.WRH_ORDER_BATA_DETAILS DROP COLUMN PRICE_TMP;
SELECT * FROM APP.WRH_ORDER_BATA_DETAILS;

ALTER TABLE APP.WRH_ORDER_BATA_DETAILS ADD COLUMN RETAIL_PRICE_TMP DECIMAL(12,4);
UPDATE APP.WRH_ORDER_BATA_DETAILS SET RETAIL_PRICE_TMP=RETAIL_PRICE;
ALTER TABLE APP.WRH_ORDER_BATA_DETAILS DROP COLUMN RETAIL_PRICE;
ALTER TABLE APP.WRH_ORDER_BATA_DETAILS ADD COLUMN RETAIL_PRICE DECIMAL(12,4);
UPDATE APP.WRH_ORDER_BATA_DETAILS SET RETAIL_PRICE=RETAIL_PRICE_TMP;
ALTER TABLE APP.WRH_ORDER_BATA_DETAILS DROP COLUMN RETAIL_PRICE_TMP;
SELECT * FROM APP.WRH_ORDER_BATA_DETAILS;

UPDATE CHANGE_LOG SET CHANGE_VAL ='ALTER TABLE WRH_ORDER_BATA_DETAILS ADD COLUMN RETAIL_PRICE DECIMAL(12,4)' WHERE ID='wrh_order_bata_details_20';
UPDATE CHANGE_LOG SET CHANGE_VAL ='ALTER TABLE WRH_ORDER_BATA_DETAILS ADD COLUMN PRICE DECIMAL(12,4)' WHERE ID='wrh_order_bata_details_22';
UPDATE CHANGE_LOG SET CHANGE_VAL ='ALTER TABLE WRH_ORDER_BATA_DETAILS ADD COLUMN POSSUM DECIMAL(12,2)' WHERE ID='wrh_order_bata_details_24';
