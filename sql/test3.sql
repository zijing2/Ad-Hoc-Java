with group0(cust, month, avg) as(
	select cust, month, avg(quant)
	from sales
	group by cust, month
),

group1(cust, month, sum, avg) as(
	select s1.cust, s1.month, sum(s2.quant), avg(s2.quant)
	from sales as s1, sales as s2
	where s1.cust = s2.cust
	and s1.month < s2.month
	group by s1.cust, s1.month
	
),

group2(cust, month, sum, avg) as(
	select s1.cust, s1.month, sum(s2.quant), avg(s2.quant)
	from sales as s1, sales as s2
	where s1.cust = s2.cust
	and s1.month > s2.month
	group by s1.cust, s1.month
	
)

select group0.cust, group0.month, floor(group1.avg), floor(group0.avg), floor(group2.avg)
from group0, group1, group2
where group0.cust = group1.cust
and group0.cust = group2.cust
and group0.month = group1.month
and group0.month = group2.month
order by group0.cust, group0.month