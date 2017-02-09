 select StockID,StockName, REPLACE(StockName,'Магазин IN UA ','') as SHORT_NAME
from r_Stocks where StockID>0 and StockID<10