
with group0(prod, avg, cnt) as(
	select prod, avg(quant), count(*)
	from sales
	group by prod
),

group2(prod, quant, avg, cnt) as(
	select s1.prod, s1.quant, avg(s2.quant), count(*)
	from sales as s1, sales as s2
	where s1.prod = s2.prod
	and s2.quant < s1.quant
	group by s1.prod, s1.quant
)

select s1.prod, s2.quant, floor(s1.avg), floor(s2.avg)
from group0 as s1, group2 as s2
where s1.prod = s2.prod
and s1.cnt/2 = s2.cnt