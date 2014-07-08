package cbit.vcell.modeldb;

/**
 * define scan status names.
 * Definitions need to match what database expects. 
 * See DDL at end of file
 */
public enum ScanStatus {
	START(0),
	PASS(1),
	FAIL(2),
	FILTERED(3),
	PROCESSING_FAILED(4);
	
	/**
	 * database code
	 */
	final int code;

	private ScanStatus(int code) {
		this.code = code;
	}
	

}
/*
--------------------------------------------------------
--  DDL for Table SCAN_STATUS
--------------------------------------------------------

  CREATE TABLE "GERARD"."SCAN_STATUS" 
   (	"CODE" NUMBER(1,0), 
	"DESCRIPTION" VARCHAR2(20 BYTE)
   ) PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
REM INSERTING into GERARD.SCAN_STATUS
SET DEFINE OFF;
Insert into GERARD.SCAN_STATUS (CODE,DESCRIPTION) values (0,'start');
Insert into GERARD.SCAN_STATUS (CODE,DESCRIPTION) values (1,'pass');
Insert into GERARD.SCAN_STATUS (CODE,DESCRIPTION) values (2,'fail');
Insert into GERARD.SCAN_STATUS (CODE,DESCRIPTION) values (3,'filtered');
--------------------------------------------------------
--  DDL for Index SCAN_STATUS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GERARD"."SCAN_STATUS_PK" ON "GERARD"."SCAN_STATUS" ("CODE") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table SCAN_STATUS
--------------------------------------------------------

  ALTER TABLE "GERARD"."SCAN_STATUS" ADD CONSTRAINT "SCAN_STATUS_PK" PRIMARY KEY ("CODE")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "GERARD"."SCAN_STATUS" MODIFY ("CODE" NOT NULL ENABLE);
 
  ALTER TABLE "GERARD"."SCAN_STATUS" MODIFY ("DESCRIPTION" NOT NULL ENABLE);
  */

