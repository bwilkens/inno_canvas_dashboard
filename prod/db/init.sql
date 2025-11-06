DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'inno-dashboard') THEN
      CREATE USER "inno-dashboard" WITH CREATEDB PASSWORD 'inno-dashboard';
   END IF;
END
$$;

DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'inno-dashboard') THEN
      CREATE DATABASE "inno-dashboard" OWNER "inno-dashboard";
   END IF;
END
$$;