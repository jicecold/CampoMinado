package modelo

import java.util.*

import kotlin.collections.ArrayList

enum class TabuleiroEvento { VITORIA, DERROTA }

class Tabuleiro(val qtdeLinhas: Int, val qtdeColunas: Int, private val qtdeMinas: Int) {
    private val campos = ArrayList<ArrayList<Campo>>()
    private val callbacks = ArrayList<(TabuleiroEvento) -> Unit>()

    init {
        geralCampos()
        associarVizinhos()
        sortearMinas()
    }

    private fun geralCampos() {
        for (linha in 0 until qtdeLinhas) {
            campos.add(ArrayList())
            for (coluna in 0 until qtdeColunas) {
                val novoCampo = Campo(linha, coluna)
                novoCampo.onEvento(this::varificarDerrotaOuVitoria)
                campos[linha].add(novoCampo)
            }
        }
    }

    private fun associarVizinhos() {
        forEachCampo { campo -> associarVizinho(campo) }
    }

    private fun associarVizinho(campo: Campo) {
        val (linha, coluna) = campo
        val linhas = arrayOf(linha - 1, linha, linha + 1)
        val colunas = arrayOf(coluna - 1, coluna, coluna + 1)

        linhas.forEach { l ->
            colunas.forEach { c ->
                val campoAtual = campos.getOrNull(l)?.getOrNull(c)
                campoAtual?.takeIf { campo != it }?.let{ campo.addVizinho(it) }
            }
        }
    }

    private fun sortearMinas() {
        val gerador = Random()

        var linhaSorteada = -1
        var colunaSorteada = -1
        var qtdeMinasAtual = 0

        while (qtdeMinasAtual < this.qtdeMinas){
            linhaSorteada = gerador.nextInt(qtdeLinhas)
            colunaSorteada = gerador.nextInt(qtdeColunas)
            val campoSorteado = campos[linhaSorteada][colunaSorteada]
            if (campoSorteado.seguro) {
                campoSorteado.minar()
                qtdeMinasAtual++
            }
        }
    }

    private fun objetivoAlcancado(): Boolean {
        var jogadorGanhou = true
        forEachCampo { if (!it.objetivoAlcancado) jogadorGanhou = false }
        return jogadorGanhou
    }

    private fun varificarDerrotaOuVitoria(campo: Campo, evento: CampoEvento) {
        if (evento == CampoEvento.EXPOSAO) {
            callbacks.forEach { it(TabuleiroEvento.DERROTA) }
        } else if (objetivoAlcancado()) {
            callbacks.forEach { it(TabuleiroEvento.VITORIA) }
        }
    }

    fun forEachCampo(callback: (Campo) -> Unit) {
        campos.forEach { linha -> linha.forEach(callback) }
    }

    fun onEvento(callback: (TabuleiroEvento) -> Unit) {
        callbacks.add(callback)
    }

    fun reinicializar() {
        forEachCampo { it.reinicializar() }
        sortearMinas()
    }
}