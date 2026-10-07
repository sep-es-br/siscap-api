-- Corrige o período PPA/LOA dos projetos que possuem registros de planejamento
-- e estão sem o campo periodo_ppa_loa preenchido.
-- Para projetos do ciclo vigente, define o período como '2024-2027'.
UPDATE projeto p
SET periodo_ppa_loa = '2024-2027'
WHERE p.criado_em >= DATE '2024-01-01'
  AND p.criado_em < DATE '2028-01-01'
  AND (p.periodo_ppa_loa IS NULL OR TRIM(p.periodo_ppa_loa) = '')
  AND EXISTS (
      SELECT 1
      FROM projeto_planejamento_ppa_loa pp
      WHERE pp.id_projeto = p.id
  );
