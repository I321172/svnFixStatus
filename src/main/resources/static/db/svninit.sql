create table if not exists fileinfo(
filepath varchar(600),
revision int(12),
type varchar(60),
copypath varchar(600),
primary key(filepath,revision)
)

create table if not exists filename(
filepath varchar(600) primary key,
filename varchar(80),
}

create table if not exists revision(
revision int(12) primary key,
author varchar(20) not null,
date date,
comment varchar(1000)
)


}
