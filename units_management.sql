-- =============================================================================
-- UNIDADES MANAGEMENT
-- =============================================================================

-- 1. View or RPC to get units with block names
-- We use an RPC to allow easy filtering/sorting if needed later, though a View could also work.
-- RPC is often safer/more flexible in Supabase for joined data.

CREATE OR REPLACE FUNCTION get_units_with_details()
RETURNS TABLE (
    id uuid,
    bloque_id uuid,
    bloque_nombre varchar,
    codigo varchar,
    piso int,
    coeficiente_area decimal,
    tipo_uso text,
    created_at timestamptz
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.id,
        u.bloque_id,
        b.nombre as bloque_nombre,
        u.codigo,
        u.piso,
        u.coeficiente_area,
        u.tipo_uso,
        u.created_at
    FROM 
        unidades u
    LEFT JOIN 
        bloques b ON u.bloque_id = b.id
    ORDER BY 
        b.nombre, u.piso, u.codigo;
END;
$$;

-- 2. RPC to get blocks for the dropdown selector
CREATE OR REPLACE FUNCTION get_blocks_selector()
RETURNS TABLE (
    id uuid,
    nombre varchar
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        b.id,
        b.nombre
    FROM 
        bloques b
    ORDER BY 
        b.nombre;
END;
$$;
