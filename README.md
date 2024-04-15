# eightbank-spring

## Código de Base - Java Back-End - Projeto Final

1 - Criar a base de dados: eightbank

2 - Rodar o script DDL para criação das tabelas

3 - Rodar os scripts das functions

4 - Subir a aplicação via IntelliJ, usando JDK 17

5 - Importar a collection no postman

## Tabelas:

CREATE TABLE ADDRESS (
	ID SERIAL PRIMARY KEY,
	STREET_NAME VARCHAR(255),
	NUMBER INTEGER,
	DISTRICT VARCHAR(50),
	CITY VARCHAR(50),
	STATE CHAR(2),
	ZIPCODE VARCHAR(10),
	ADDRESS_COMPLEMENT TEXT
);

CREATE TABLE CLIENT (
	CPF CHAR(11) PRIMARY KEY,
	NAME VARCHAR(255),
	EMAIL VARCHAR(255),
	PASSWORD VARCHAR(255),
	DATE_OF_BIRTH DATE, 
	CLIENT_CATEGORY VARCHAR(255),
	PHONE_NUMBER VARCHAR(20),
	GROSS_MONTHLY_INCOME NUMERIC(10,2),
	ADDRESS_ID INT REFERENCES ADDRESS(ID)
);

CREATE TABLE CURRENT_ACCOUNT (
	ACCOUNT_NUMBER PRIMARY KEY,
	BALANCE NUMERIC(10,2),
	ACCOUNT_TYPE VARCHAR(255),
	ACCOUNT_FEE NUMERIC(10,2),
	CLIENT_ID SERIAL REFERENCES CLIENT(id)
);

CREATE TABLE SAVINGS_ACCOUNT (
	ACCOUNT_NUMBER PRIMARY KEY,
	BALANCE NUMERIC(10,2),
	ACCOUNT_TYPE VARCHAR(255),
	SAVINGS_ACCOUNT_ANNUAL_PERCENTAGE_YIELD NUMERIC(10,2),
	CLIENT_ID SERIAL REFERENCES CLIENT(id)
);

## Functions:

--Eightbank SQL Functions

-- FUNCTION: public.save_client(character, character varying, character varying, character varying, date, character varying, character varying, numeric, character varying, integer, character varying, character varying, character, character varying, text)

-- DROP FUNCTION IF EXISTS public.save_client(character, character varying, character varying, character varying, date, character varying, character varying, numeric, character varying, integer, character varying, character varying, character, character varying, text);

CREATE OR REPLACE FUNCTION public.save_client(
	c_cpf character,
	c_name character varying,
	c_email character varying,
	c_password character varying,
	c_date_of_birth date,
	c_client_category character varying,
	c_phone_number character varying,
	c_gross_monthly_income numeric,
	a_street_name character varying,
	a_number integer,
	a_district character varying,
	a_city character varying,
	a_state character,
	a_zipcode character varying,
	a_address_complement text)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
	v_address_id INT;
BEGIN
INSERT INTO ADDRESS(
	STREET_NAME,
	NUMBER,
	DISTRICT,
	CITY,
	STATE,
	ZIPCODE,
	ADDRESS_COMPLEMENT
) VALUES (
	A_STREET_NAME,
	A_NUMBER,
	A_DISTRICT,
	A_CITY,
	A_STATE,
	A_ZIPCODE,
	A_ADDRESS_COMPLEMENT
) RETURNING id INTO v_address_id;

INSERT INTO CLIENT (
	CPF, 
	NAME,
	EMAIL,
	PASSWORD,
	DATE_OF_BIRTH,
	CLIENT_CATEGORY,
	PHONE_NUMBER,
	GROSS_MONTHLY_INCOME,
	ADDRESS_ID
) VALUES (
	C_CPF,
	C_NAME,
	C_EMAIL,
	C_PASSWORD,
	C_DATE_OF_BIRTH, 
	C_CLIENT_CATEGORY,
	C_PHONE_NUMBER,
	C_GROSS_MONTHLY_INCOME,
	v_address_id
);
	
END;
$BODY$;

ALTER FUNCTION public.save_client(character, character varying, character varying, character varying, date, character varying, character varying, numeric, character varying, integer, character varying, character varying, character, character varying, text)
    OWNER TO postgres;

--------------------------------------------------

-- FUNCTION: public.save_current_account(character, numeric, character varying, numeric)

-- DROP FUNCTION IF EXISTS public.save_current_account(character, numeric, character varying, numeric);

CREATE OR REPLACE FUNCTION public.save_current_account(
	c_cpf character,
	a_balance numeric,
	a_account_type character varying,
	a_account_fee numeric)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
BEGIN

INSERT INTO CURRENT_ACCOUNT(
	BALANCE,
	ACCOUNT_TYPE,
	ACCOUNT_FEE,
	OWNER_CPF
) VALUES (
	A_BALANCE,
	A_ACCOUNT_TYPE,
	A_ACCOUNT_FEE,
	C_CPF
);

END;
$BODY$;

ALTER FUNCTION public.save_current_account(character, numeric, character varying, numeric)
    OWNER TO postgres;
-------------------------------------------------------------

-- FUNCTION: public.save_savings_account(character, numeric, character varying, numeric)

-- DROP FUNCTION IF EXISTS public.save_savings_account(character, numeric, character varying, numeric);

CREATE OR REPLACE FUNCTION public.save_savings_account(
	c_cpf character,
	a_balance numeric,
	a_account_type character varying,
	a_annual_percentage_yield numeric)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$

BEGIN
INSERT INTO SAVINGS_ACCOUNT(
	BALANCE,
	ACCOUNT_TYPE,
	SAVINGS_ACCOUNT_ANNUAL_PERCENTAGE_YIELD,
	OWNER_CPF
) VALUES (
	A_BALANCE,
	A_ACCOUNT_TYPE,
	A_ANNUAL_PERCENTAGE_YIELD,
	C_CPF
);

END;
$BODY$;

ALTER FUNCTION public.save_savings_account(character, numeric, character varying, numeric)
    OWNER TO postgres;

----------------------------------------------------------------------

-- FUNCTION: public.update_current_account_balance(integer, numeric)

-- DROP FUNCTION IF EXISTS public.update_current_account_balance(integer, numeric);

CREATE OR REPLACE FUNCTION public.update_current_account_balance(
	a_account_number integer,
	a_value numeric)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$

BEGIN

UPDATE CURRENT_ACCOUNT
SET BALANCE = A_VALUE
WHERE ACCOUNT_NUMBER = A_ACCOUNT_NUMBER;

END;
$BODY$;

ALTER FUNCTION public.update_current_account_balance(integer, numeric)
    OWNER TO postgres;


-----------------------------------------------------------------------------

-- FUNCTION: public.update_savings_account_balance(integer, numeric)

-- DROP FUNCTION IF EXISTS public.update_savings_account_balance(integer, numeric);

CREATE OR REPLACE FUNCTION public.update_savings_account_balance(
	a_account_number integer,
	a_value numeric)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$

BEGIN

UPDATE SAVINGS_ACCOUNT
SET BALANCE = A_VALUE
WHERE ACCOUNT_NUMBER = A_ACCOUNT_NUMBER;

END;
$BODY$;

ALTER FUNCTION public.update_savings_account_balance(integer, numeric)
    OWNER TO postgres;
