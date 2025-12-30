-- Función para obtener estadísticas del Dashboard de Administrador
-- Retorna un JSON con los indicadores clave de rendimiento (KPIs)

CREATE OR REPLACE FUNCTION get_admin_dashboard_stats()
RETURNS TABLE (
    collection_amount numeric,
    collection_target numeric,
    pending_validations bigint,
    overdue_amount numeric,
    overdue_count bigint,
    current_period_name text,
    period_status text
) 
LANGUAGE plpgsql
AS $$
DECLARE
    -- Variables para el periodo actual
    v_periodo_id bigint;
    v_mes int;
    v_anio int;
    v_periodo_nombre text;
    v_periodo_estado text;
    
    -- Variables para cálculos
    v_recaudado numeric := 0;
    v_objetivo numeric := 0;
    v_validaciones bigint := 0;
    v_deuda_vencida numeric := 0;
    v_conteo_morosos bigint := 0;
    
    -- Array de meses para formateo
    v_meses text[] := ARRAY['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
BEGIN
    -- 1. Identificar el Periodo Actual (EN_PROCESO o el último ABIERTO/CERRADO si no hay proceso)
    SELECT id, mes, anio, estado
    INTO v_periodo_id, v_mes, v_anio, v_periodo_estado
    FROM public.periodos
    WHERE estado = 'EN_PROCESO'
    ORDER BY id DESC
    LIMIT 1;

    -- Si no hay periodo en proceso, buscar el último periodo (para mostrar datos históricos recientes)
    IF v_periodo_id IS NULL THEN
        SELECT id, mes, anio, estado
        INTO v_periodo_id, v_mes, v_anio, v_periodo_estado
        FROM public.periodos
        ORDER BY id DESC
        LIMIT 1;
    END IF;

    -- Formatear nombre del periodo
    IF v_periodo_id IS NOT NULL THEN
        v_periodo_nombre := v_meses[v_mes] || ' ' || v_anio;
    ELSE
        v_periodo_nombre := 'Sin Periodo';
        v_periodo_estado := 'N/A';
    END IF;

    -- 2. Calcular Recaudación del Periodo Actual (si existe)
    IF v_periodo_id IS NOT NULL THEN
        -- Objetivo Total: Suma de total_pagar de todos los recibos del periodo (excluyendo anulados)
        SELECT COALESCE(SUM(total_pagar), 0)
        INTO v_objetivo
        FROM public.recibos_cabecera
        WHERE periodo_id = v_periodo_id
        AND estado_pago != 'ANULADO';

        -- Recaudado Real: Suma de lo pagado en el periodo (Simplificado: Recibos con estado PAGADO)
        -- Nota: Para mayor precisión en pagos parciales, se debería sumar transacciones aprobadas, 
        -- pero basándonos en el esquema actual y estado_pago:
        SELECT COALESCE(SUM(total_pagar), 0)
        INTO v_recaudado
        FROM public.recibos_cabecera
        WHERE periodo_id = v_periodo_id
        AND estado_pago = 'PAGADO';
    END IF;

    -- 3. Calcular Validaciones Pendientes (Transacciones por aprobar)
    SELECT COUNT(*)
    INTO v_validaciones
    FROM public.transacciones
    WHERE estado_validacion = 'PENDIENTE';

    -- 4. Calcular Morosidad (Deuda Vencida)
    -- Consideramos deuda vencida a recibos NO PAGADOS cuya fecha de vencimiento ya pasó
    -- Y que no sean del periodo actual (opcional, dependiendo de regla de negocio, aquí asumimos todo lo vencido)
    SELECT 
        COALESCE(SUM(total_pagar), 0),
        COUNT(DISTINCT unidad_id)
    INTO v_deuda_vencida, v_conteo_morosos
    FROM public.recibos_cabecera
    WHERE estado_pago IN ('PENDIENTE', 'PARCIAL')
    AND fecha_vencimiento < CURRENT_DATE
    AND estado_pago != 'ANULADO';

    -- Retornar resultados
    RETURN QUERY SELECT 
        v_recaudado, 
        v_objetivo, 
        v_validaciones, 
        v_deuda_vencida, 
        v_conteo_morosos, 
        v_periodo_nombre,
        v_periodo_estado;
END;
$$;
