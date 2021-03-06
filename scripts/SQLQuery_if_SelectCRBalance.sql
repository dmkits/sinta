USE [GMSData38_20161130_1320]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

--CREATE
ALTER 
	FUNCTION [dbo].[if_SelectCRBalance](@CRID int, @BDate datetime, @EDate datetime)
RETURNS @out TABLE(ItemID int, ItemName varchar(200), SumCC numeric(21,9), url varchar(200)) 
AS BEGIN   
	DECLARE @SaleSumCash numeric(19, 9), @SaleSumCCard numeric(19, 9), @SaleSumCredit numeric(19, 9), @SaleSumCheque numeric(19, 9), @SaleSumOther numeric(19, 9)
		,@MRec numeric(19, 9), @MExp numeric(19, 9)
		,@SumCash numeric(19, 9), @SumRetCash numeric(19, 9), @SumRetCCard numeric(19, 9), @SumRetCredit numeric(19, 9), @SumRetCheque numeric(19, 9), @SumRetOther numeric(19, 9)

	SELECT @SaleSumCash = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_Sale m, t_SalePays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode = 1	SELECT @SaleSumCCard = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_Sale m, t_SalePays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode = 2	SELECT @SaleSumCredit = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_Sale m, t_SalePays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode = 3	SELECT @SaleSumCheque = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_Sale m, t_SalePays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode = 4	SELECT @SaleSumOther = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_Sale m, t_SalePays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode NOT IN (1, 2, 3, 4)	SELECT @SumRetCash = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_CRRet m, t_CRRetPays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode = 1	SELECT @SumRetCCard = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_CRRet m, t_CRRetPays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode = 2	SELECT @SumRetCredit = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_CRRet m, t_CRRetPays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode = 3	SELECT @SumRetCheque = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_CRRet m, t_CRRetPays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode = 4	SELECT @SumRetOther = ROUND(ISNULL(Sum(d.SumCC_wt), 0), 2) 		FROM t_CRRet m, t_CRRetPays d WHERE m.ChID = d.ChID AND m.DocDate BETWEEN @BDate AND @EDate AND m.CRID = @CRID AND d.PayFormCode NOT IN (1, 2, 3, 4)	SELECT @SaleSumCash = @SaleSumCash - @SumRetCash	SELECT @SaleSumCCard = @SaleSumCCard - @SumRetCCard	SELECT @SaleSumCredit = @SaleSumCredit - @SumRetCredit	SELECT @SaleSumCheque = @SaleSumCheque - @SumRetCheque	SELECT @SaleSumOther = @SaleSumOther - @SumRetOther	SELECT @MRec = ISNULL(Sum(SumCC), 0) FROM t_MonIntRec WHERE DocDate BETWEEN @BDate AND @EDate AND CRID = @CRID  	SELECT @MExp = ISNULL(Sum(SumCC), 0) FROM t_MonIntExp WHERE DocDate BETWEEN @BDate AND @EDate AND CRID = @CRID	SELECT @SumCash = @SaleSumCash + @MRec - @MExp
	INSERT INTO @out
		SELECT 1, 'Выручка', (@SaleSumCash + @SaleSumCCard + @SaleSumCredit + @SaleSumCheque + @SaleSumOther), '/mobile/total_sales_sum.json'
		UNION ALL
		SELECT 2, 'Выручка (наличные)', @SaleSumCash, '/mobile/total_sales_cash_sum.json'
		UNION ALL
		SELECT 3, 'Выручка (плат.карты)', @SaleSumCCard, null
		UNION ALL
		SELECT 4, 'Выручка (кредит)', @SaleSumCredit, null
		UNION ALL
		SELECT 5, 'Выручка (чеки)', @SaleSumCheque , null
		UNION ALL
		SELECT 6, 'Выручка (другое)', @SaleSumOther, null
		UNION ALL
		SELECT 7, 'Вносы/выносы', @MRec-@MExp, null
		UNION ALL
		SELECT 8, 'Наличность в кассе', @SumCash, null
	RETURN
END     