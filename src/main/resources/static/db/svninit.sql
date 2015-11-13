create table if not exists fileinfo(
filepath varchar(600),
revision int(12),
type varchar(60),
copypath varchar(600),
primary key(filepath,revision)
)

create table if not exists filename(
filepath varchar(600) primary key,
filename varchar(300),
revision int(12),
invalid bit
)

create table if not exists revision(
revision int(12) primary key,
author varchar(40) not null,
date datetime,
comment varchar(1000)
)


#regular query filename
insert into filename(filepath,filename) select distinct filepath,substring_index(filepath,'/',-1) from fileinfo where filepath not in (select filepath from filename);

update filename set invalid=true where (invalid is null or invalid =false) and filepath in (select filepath from (select filepath,type from (select filepath,type from fileinfo order by revision desc) as t1 group by filepath) as t2 where type='Delete');
 
update filename set invalid=false where invalid=true and filepath not in (select filepath from (select filepath,type from (select filepath,type from fileinfo order by revision desc) as t1 group by filepath) as t2 where type='Delete')