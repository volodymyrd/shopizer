### POSTGRESQL

```
createdb SHOPIZER
create database "SHOPIZER";
(dropdb SHOPIZER)
psql SHOPIZER
\l
CREATE SCHEMA IF NOT EXISTS SALESMANAGER;
\dn
REVOKE ALL ON schema SALESMANAGER FROM public;
CREATE USER shopizer_db_user WITH PASSWORD '15ih5BO8KbJh4smr';
\du
(DROP ROLE salesmanager)
GRANT ALL ON schema SALESMANAGER TO shopizer_db_user;
ALTER ROLE shopizer_db_user SET search_path = SALESMANAGER;
```

#### DOCKER

```
docker pull postgres:14.2
```

```
docker run -itd -e POSTGRES_USER=vova -e POSTGRES_PASSWORD=vova -e POSTGRES_HOST_AUTH_METHOD=trust \
-p 54320:5432 -v local_psql_data:/var/lib/postgresql/data --name local-psql postgres:14.2
```

```
docker logs -f local-psql
```

```
docker exec -it local-psql psql -U vova
```

### Useful docker commands

```
docker images
```

- List all containers (only IDs)

```
docker ps -aq
```

- Stop all running containers

```
docker stop $(docker ps -aq)
```

- Remove all containers

```
docker rm $(docker ps -aq)
```

- Remove all images

```
docker rmi $(docker images -q)
```
