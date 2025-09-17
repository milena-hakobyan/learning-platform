-- task#3a Country with the biggest population (id and name of the country)
SELECT id, name 
FROM countries
WHERE population = (SELECT max(population)
					FROM countries);
--another version
SELECT id, name
FROM countries
ORDER BY population DESC
LIMIT 1;




-- task#3b Top 10 countries with the lowest population density (names of the countries)
SELECT name,
       ROUND(population / NULLIF(area, 0),2) AS population_density
FROM countries
ORDER BY population_density
LIMIT 10;

--another version where only the name is appearing, not the density 
SELECT name
FROM countries
WHERE area > 0
ORDER BY population/area
LIMIT 10;




-- task#3c Countries with population density higher than average across all countries
SELECT *, population / area AS density
FROM countries
WHERE area > 0
  AND (population / area) > (
    SELECT AVG(population / NULLIF(area, 0))
    FROM countries
  );



-- task#3d All countries with name containing letter “F”, sorted in alphabetical order
SELECT *
FROM countries
WHERE name ILIKE '%f%'
ORDER BY name;



-- task#3e Country which has a population, closest to the average population of all countries
WITH avg_population(value) AS (
	SELECT AVG(population)
	FROM countries
)
SELECT *
FROM countries, avg_population
ORDER BY ABS(population-avg_population.value)
LIMIT 1;

--another version

SELECT *
FROM countries
ORDER BY ABS(population - (
	SELECT AVG(population)
	FROM countries))
LIMIT 1;




--task#4a Count of countries for each continent
SELECT con.name AS continent_name, COUNT(*) AS country_count
FROM countries c 
	JOIN continents con ON c.continent_id = con.id
GROUP BY con.id, con.name;



--task#4b Total area for each continent (print continent name and total area), sorted by area from biggest to smallest
SELECT con.name as continent_name, SUM(c.area) as total_area
FROM countries c 
	JOIN continents con ON c.continent_id = con.id
GROUP BY con.id, con.name
ORDER BY total_area DESC;




--task#4c Average population density per continent
SELECT con.name, SUM(c.population)/SUM(c.area) as population_density
FROM countries c 
	JOIN continents con ON c.continent_id = con.id
GROUP BY con.name;



--task#4d For each continent, find a country with the smallest area (print continent name, country name and area)
WITH min_areas AS (
    SELECT continent_id, MIN(area) AS min_area
    FROM countries
    GROUP BY continent_id
)
SELECT con.name AS continent_name, c.name AS country_name, c.area
FROM countries c
	JOIN min_areas ma ON c.continent_id = ma.continent_id AND c.area = ma.min_area
	JOIN continents con ON c.continent_id = con.id;

	

--task#4eFind all continents, which have average country population less than 20 million
SELECT con.name, AVG(c.population) as avg_country_population
FROM countries c 
	INNER JOIN continents con ON c.continent_id = con.id
GROUP BY con.name
HAVING AVG(c.population) < 20000000;



--task#5a Person with the biggest number of citizenships
WITH citizenship_counts AS (
	SELECT p.*, COUNT(*) AS cnt
	FROM person_citizenships pc
		JOIN people p ON pc.person_id = p.id
	GROUP BY p.id
),
max_count AS (
	SELECT MAX(cnt) AS max_cnt FROM citizenship_counts
)
SELECT cc.*
FROM citizenship_counts cc
JOIN max_count mc ON cc.cnt = mc.max_cnt;



--task#5b All people who have no citizenship
SELECT p.*
FROM people p 
	LEFT JOIN person_citizenships pc ON p.id = pc.person_id
WHERE pc.person_id IS NULL;



--task#5c Country with the least people in People table
WITH country_people_counts AS (
	SELECT c.id, c.name, COUNT(pc.person_id) ppl_count
	FROM countries c
		JOIN person_citizenships pc ON c.id = pc.country_id
	GROUP BY c.id, c.name
),
min_count(value) AS (
	SELECT MIN(ppl_count)
	FROM country_people_counts
)
SELECT cpc.*
FROM country_people_counts cpc
	JOIN min_count mc ON cpc.ppl_count = mc.value;


--task#5d Continent with the most people in People table
WITH country_people_counts AS (
    SELECT
        c.id AS country_id,
        COUNT(pc.person_id) AS people_count  -- count people explicitly
    FROM countries c
    JOIN person_citizenships pc ON c.id = pc.country_id
    GROUP BY c.id
),
continent_count AS (
	FROM country_people_counts cpc
		JOIN countries c ON c.id = cpc.country_id
),
max_count AS (
    SELECT MAX(total_people) AS max_people
    FROM continent_count
)
SELECT
    cn.*,
    cc.total_people
FROM continent_count cc
	JOIN max_count mc ON cc.total_people = mc.max_people
	JOIN continents cn ON cn.id = cc.continent_id;




--task#5e Find pairs of people with the same name - print 2 ids and the name
SELECT p1.id, p2.id, p1.first_name, p1.last_name
FROM people p1
	JOIN people p2
	ON p1.first_name = p2.first_name
	AND p1.last_name = p2.last_name
 	AND p1.id < p2.id
