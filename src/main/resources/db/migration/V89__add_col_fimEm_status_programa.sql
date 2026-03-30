ALTER TABLE public.programa_status
ADD COLUMN fim_em TIMESTAMP NULL;

UPDATE public.programa_status
SET fim_em = inicio_em
WHERE id_pessoa IS NOT NULL