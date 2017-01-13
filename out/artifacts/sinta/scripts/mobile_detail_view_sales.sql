DECLARE @BDATE datetime, @EDATE datetime, @StocksList VARCHAR(200)

SELECT @BDATE=?, @EDATE=?, @StocksList=?

select pc.PCatName as label, sum(sd.SumCC_wt) as value
from t_SaleD sd
INNER JOIN t_Sale s ON s.ChID=sd.ChID
INNER JOIN r_Prods p ON p.ProdID=sd.ProdID
INNER JOIN r_ProdC pc on pc.PCatID=p.PCatID
WHERE s.DocDate BETWEEN @BDATE AND @EDATE
  AND @StocksList like '% '+CAST(s.StockID as varchar(200))+' %'
GROUP BY pc.PCatName
ORDER BY value DESC