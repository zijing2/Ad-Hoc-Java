with group1(cust, prod, sum, avg)  as(
	select cust, prod, sum(quant), avg(quant)
	from sales
	where state = 'NY'
	group by cust, prod
),

group2(cust, prod, sum, avg) as(
	select cust, prod, sum(quant), avg(quant)
	from sales
	where state = 'NJ'
	group by cust,prod
),

group3(cust, prod, sum, avg) as(
	select cust, prod, sum(quant), avg(quant)
	from sales
	where state = 'CT'
	group by cust, prod
)

select group1.cust, group1.prod, floor(group1.avg), floor(group2.avg), floor(group3.avg)
from group1, group2, group3
where group1.cust = group2.cust
and group1.cust = group3.cust
and group1.prod = group2.prod
and group1.prod = group3.prod
and group1.avg > group2.avg
and group1.avg > group3.avg
order by group1.cust, group1.prod