create index rev on revision (author,date);

create index rev on fileinfo(revision);

drop index rev on revision;