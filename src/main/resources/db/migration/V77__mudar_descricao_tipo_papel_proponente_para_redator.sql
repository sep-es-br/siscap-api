-- 15/01/2026
-- vamos mudar o nome do tipo de papel 'Proponente'
-- para 'Redator' para identificar melhor que criou o DIC
update tipo_papel
    set tipo = 'Redator'
    where tipo = 'Proponente'