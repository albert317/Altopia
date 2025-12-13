-- =============================================================================
-- STORED PROCEDURE: Obtener Roles de Usuario
-- =============================================================================

CREATE OR REPLACE FUNCTION get_user_roles(p_usuario_id UUID)
RETURNS TABLE (
    usuario_id UUID,
    rol_asignado VARCHAR
) 
LANGUAGE plpgsql
SECURITY DEFINER -- Ejecuta con privilegios del creador (útil para exponer vía API si fuera necesario)
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.id as usuario_id,
        r.nombre as rol_asignado
    FROM usuarios u
    LEFT JOIN usuario_roles ur ON u.id = ur.usuario_id
    LEFT JOIN roles r ON ur.rol_id = r.id
    WHERE u.id = p_usuario_id;
END;
$$;

-- Ejemplo de uso:
-- SELECT * FROM get_user_roles('33d16dde-3623-4966-a6bc-a5bff4b13a58');
