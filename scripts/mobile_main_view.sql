DECLARE @BDATE datetime, @EDATE datetime, @StocksList VARCHAR(200)

SELECT @BDATE=?, @EDATE=?,  @StocksList=?

select 'sales' as id, 'Реализация' as label,'sales' as DETAIL_ID, COALESCE(SUM(TSumCC_wt),0) as value
  from t_Sale where DocDate BETWEEN @BDATE  AND @EDATE
  AND @StocksList like '% '+CAST(StockID as varchar(200))+' %'

UNION ALL
select 'returns' as id, 'Возвраты' as label,'returns' as DETAIL_ID, COALESCE(SUM(TSumCC_wt),0) as value
  from t_CRRet where DocDate BETWEEN @BDATE  AND @EDATE
   AND @StocksList like '% '+CAST(StockID as varchar(200))+' %'

UNION ALL
SELECT 'cash_income' as id, 'Выручка нал' as label,'cash' as DETAIL_ID,
    (SELECT COALESCE(SUM(pays.SumCC_wt),0)
        FROM t_SalePays pays
    INNER JOIN t_Sales sales ON sales.ChID=pays.ChID
         WHERE pays.PayformCode=1 AND sales.DocDate BETWEEN @BDATE   AND @EDATE
          AND @StocksList like '% '+CAST(sales.StockID as varchar(200))+' %'
 )
   - (SELECT COALESCE(sum(pays.SumCC_wt),0)
        FROM t_CRRetPays pays
    INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID
        WHERE pays.PayformCode=1 AND returns.DocDate BETWEEN @BDATE   AND @EDATE
         AND @StocksList like '% '+CAST(returns.StockID as varchar(200))+' %'
 ) as value
 --CASH_INCOME

 UNION ALL
SELECT  'card_income' as id,  'Выручка ПК' as label,'card' as DETAIL_ID,
    (SELECT COALESCE(SUM(pays.SumCC_wt),0)
         FROM t_SalePays pays
    INNER JOIN t_Sales sales ON sales.ChID=pays.ChID
         WHERE pays.PayformCode=2 AND sales.DocDate BETWEEN @BDATE  AND @EDATE
         AND @StocksList like '% '+CAST(sales.StockID as varchar(200))+' %'
)
    -(SELECT COALESCE(sum(pays.SumCC_wt),0)
        FROM t_CRRetPays pays
    INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID
        WHERE pays.PayformCode=2 AND returns.DocDate BETWEEN @BDATE  AND @EDATE
        AND @StocksList like '% '+CAST(returns.StockID as varchar(200))+' %'
 )as value
 --CARD_INCOME

UNION ALL
SELECT 'other_income' as id, 'Выручка прочее' as label,  'other' as DETAIL_ID,

     (SELECT COALESCE(SUM(pays.SumCC_wt),0)
        FROM t_SalePays pays
    INNER JOIN t_Sales sales ON sales.ChID=pays.ChID
      WHERE NOT pays.PayformCode  in (1,2)
      AND sales.DocDate BETWEEN @BDATE  AND @EDATE
      AND @StocksList like '% '+CAST(sales.StockID as varchar(200))+' %'
 )
  - (SELECT COALESCE (sum(pays.SumCC_wt),0)
      FROM t_CRRetPays pays
  INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID
       WHERE NOT pays.PayformCode  in (1,2)
       AND returns.DocDate BETWEEN @BDATE  AND @EDATE
       AND @StocksList like '% '+CAST(returns.StockID as varchar(200))+' %'
        ) as value
  --OTHER_INCOME

