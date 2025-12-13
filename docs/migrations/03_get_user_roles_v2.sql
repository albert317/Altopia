-- =============================================================================
-- STORED PROCEDURE: Obtener Roles de Usuario (Compatible Supabase)
-- =============================================================================

-- 1. Primero, verifica que la consulta simple funcione.
-- Ejecuta esto por separado reemplazando el ID:
-- SELECT r.nombre FROM public.roles r 
-- JOIN public.usuario_roles ur ON ur.rol_id = r.id 
-- WHERE ur.usuario_id = 'TU_UUID_AQUI';

-- 2. Creación de la Función (RPC)
-- Usamos 'public.' para evitar ambigüedades.
-- Retornamos JSON o TABLE. Aquí retornamos una tabla simple.

CREATE OR REPLACE FUNCTION public.get_user_roles(p_user_id uuid)
RETURNS TABLE (
    role_name text
) 
LANGUAGE plpgsql
SECURITY DEFINER -- Permite leer roles aunque el usuario no tenga acceso directo a la tabla
SET search_path = public -- Buena práctica: define el schema explícitamente
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        r.nombre::text -- Casteo explícito a text para coincidir con RETURNS
    FROM public.roles r
    INNER JOIN public.usuario_roles ur ON ur.rol_id = r.id
    WHERE ur.usuario_id = p_user_id;
END;
$$;
