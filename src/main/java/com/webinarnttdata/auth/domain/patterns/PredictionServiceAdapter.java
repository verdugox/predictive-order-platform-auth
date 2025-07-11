package com.webinarnttdata.auth.domain.patterns;

public class PredictionServiceAdapter implements ExternalServiceAdapter {

    @Override
    public String fetchData() {
        // Aquí iría la lógica para interactuar con el servicio de predicciones
        // Por ejemplo, hacer una llamada HTTP a un endpoint de predicciones
        return "Respuesta simulada desde Prediction Service";
    }
}
