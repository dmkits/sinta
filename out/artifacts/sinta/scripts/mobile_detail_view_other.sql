DECLARE @BDATE datetime, @EDATE datetime, @StocksList VARCHAR(200)

SELECT @BDATE=?, @EDATE=?, @StocksList=?

SELECT  'Продажи прочее' as LABEL,SUM(pays.SumCC_wt) AS VALUE
FROM t_SalePays pays
INNER JOIN t_Sales sales ON sales.ChID=pays.ChID
WHERE pays.PayformCode NOT in (1,2)
AND sales.DocDate BETWEEN  @BDATE  AND @EDATE
AND @StocksList like '% '+CAST(sales.StockID as varchar(200))+' %'

UNION ALL
SELECT 'Возвраты прочее' as LABEL,  sum(pays.SumCC_wt) AS VALUE
FROM t_CRRetPays pays
INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID
WHERE pays.PayformCode NOT in (1,2)
AND returns.DocDate BETWEEN  @BDATE  AND @EDATE
AND @StocksList like '% '+CAST(returns.StockID as varchar(200))+' %'