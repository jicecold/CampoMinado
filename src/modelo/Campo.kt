package modelo

enum class CampoEvento { ABERTURA, MARCACAO, DESMACACAO, EXPOSAO, REINICIALIZACAO }

data class Campo(val linha: Int, val coluna: Int) {

    private val vizinhos = ArrayList<Campo>()
    private val callbacks = ArrayList<(Campo, CampoEvento) -> Unit>()

    var marcado: Boolean = false
    var aberto: Boolean = false
    var minado: Boolean = false

    val desmarcado: Boolean get() = !marcado
    val fechado: Boolean get() = !aberto
    val seguro: Boolean get() = !minado
    val objetivoAlcancado: Boolean get() = seguro && aberto || minado && marcado
    val qtdDeVizinhosMinados: Int get() = vizinhos.filter { it.minado }.size
    val vizinhacaSegura: Boolean
        get() = vizinhos.map { it.seguro }.reduce { resultado, seguro -> resultado && seguro }

    fun addVizinho(vizinho: Campo) {
        vizinhos.add(vizinho)
    }

    fun onEvento(callback: (Campo, CampoEvento) -> Unit) {
        callbacks.add(callback)
    }

    fun abrir() {
        if (fechado) {
            aberto = true
            if (minado) {
                callbacks.forEach { callback -> callback(this, CampoEvento.EXPOSAO) }
            } else {
                callbacks.forEach { callback -> callback(this, CampoEvento.ABERTURA) }
                vizinhos.filter { vizinho -> vizinho.fechado && vizinho.seguro && vizinhacaSegura }
                    .forEach { vizinho -> vizinho.abrir() }
            }
        }
    }

    fun alterarMarcacao() {
        if (fechado) {
            marcado = !marcado
            val evento = if (marcado) CampoEvento.MARCACAO else CampoEvento.DESMACACAO
            callbacks.forEach { callback -> callback(this, evento) }
        }
    }

    fun minar() {
        minado = true
    }

    fun reinicializar() {
        aberto = false
        minado = false
        marcado = false
        callbacks.forEach { callback -> callback(this, CampoEvento.REINICIALIZACAO) }
    }

}
