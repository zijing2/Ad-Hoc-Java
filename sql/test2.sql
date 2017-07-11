

with s1(prod, month, sum) as(
	select prod, month, sum(quant)
	from sales
	group by prod, month
),
s2(prod, sum) as(
	select prod, sum(quant)
	from sales
	group by prod
)

select s1.prod, s1.month, s1.sum, s2.sum
from s1, s2
where s1.prod = s2.prod
order by s1.prod, s1.month