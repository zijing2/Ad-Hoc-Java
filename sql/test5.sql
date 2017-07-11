with group0(cust, prod, avg) as(
	select cust, prod, avg(quant)
	from sales
	group by cust, prod
),

group1(cust, prod, avg) as(
	select s1.cust, s1.prod, avg(s2.quant)
	from sales as s1, sales as s2
	where s1.cust <> s2.cust
	and s1.prod = s2.prod 
	group by s1.cust, s1.prod
)

select group0.cust, group0.prod, floor(group0.avg), floor(group1.avg)
from group0, group1
where group0.cust = group1.cust
and group0.prod = group1.prod
order by group0.cust, group0.prod





