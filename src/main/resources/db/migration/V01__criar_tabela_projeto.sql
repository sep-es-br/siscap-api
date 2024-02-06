CREATE TABLE project
(
    "id"              serial        NOT NULL UNIQUE,
    "acronym"         varchar(12)   NOT NULL UNIQUE,
    "title"           varchar(150)  NOT NULL,
    "estimated_value" bigint        NOT NULL,
    "goal"            varchar(2000) NOT NULL,
    "specific_goal"   varchar(2000) NOT NULL,
    "created_at"      TIMESTAMP     NOT NULL,
    "updated_at"      TIMESTAMP     NOT NULL,
    "deleted_at"      TIMESTAMP     NOT NULL,
    CONSTRAINT "project_pk" PRIMARY KEY ("id")
);