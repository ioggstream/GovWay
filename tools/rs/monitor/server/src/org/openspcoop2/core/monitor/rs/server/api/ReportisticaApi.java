package org.openspcoop2.core.monitor.rs.server.api;

import org.openspcoop2.core.monitor.rs.server.model.Fault;
import org.openspcoop2.core.monitor.rs.server.model.GenerazioneReport;
import org.openspcoop2.core.monitor.rs.server.model.ReportGrafico;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.ext.multipart.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * GovWay Monitor API
 *
 * <p>Servizi per il monitoraggio di GovWay
 *
 */
@Path("/")
public interface ReportisticaApi  {

    /**
     * genera report statistico
     *
     * Questa operazione consente di generare un report statistico
     *
     */
    @POST
    @Path("/statistiche/report")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "genera report statistico", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "report generato correttamente", content = @Content(schema = @Schema(implementation = ReportGrafico.class))),
        @ApiResponse(responseCode = "400", description = "La richiesta inviata ha prodotto un errore", content = @Content(schema = @Schema(implementation = Fault.class))) })
    public ReportGrafico generaReport(@Valid GenerazioneReport body);
}
