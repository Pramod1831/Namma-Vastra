create extension if not exists pgcrypto;

create table if not exists public.weavers (
  id text primary key,
  name text not null default '',
  village text not null default '',
  image_url text not null default '',
  subtitle text not null default ''
);

create table if not exists public.story_submissions (
  id text primary key,
  title text not null default '',
  content text not null default '',
  section text not null default '',
  weaver_name text not null default '',
  village text not null default '',
  subtitle text not null default '',
  image_url text not null default '',
  submitted_by text not null default '',
  status text not null default 'pending'
);

alter table public.weavers enable row level security;
alter table public.story_submissions enable row level security;
alter table public.sarees enable row level security;

drop policy if exists "public read weavers" on public.weavers;
create policy "public read weavers"
on public.weavers
for select
to anon
using (true);

drop policy if exists "public insert weavers" on public.weavers;
create policy "public insert weavers"
on public.weavers
for insert
to anon
with check (true);

drop policy if exists "public delete weavers" on public.weavers;
create policy "public delete weavers"
on public.weavers
for delete
to anon
using (true);

drop policy if exists "public read pending story submissions" on public.story_submissions;
create policy "public read pending story submissions"
on public.story_submissions
for select
to anon
using (true);

drop policy if exists "public insert story submissions" on public.story_submissions;
create policy "public insert story submissions"
on public.story_submissions
for insert
to anon
with check (true);

drop policy if exists "public delete story submissions" on public.story_submissions;
create policy "public delete story submissions"
on public.story_submissions
for delete
to anon
using (true);

drop policy if exists "public read sarees" on public.sarees;
create policy "public read sarees"
on public.sarees
for select
to anon
using (true);

drop policy if exists "public delete sarees" on public.sarees;
create policy "public delete sarees"
on public.sarees
for delete
to anon
using (true);
