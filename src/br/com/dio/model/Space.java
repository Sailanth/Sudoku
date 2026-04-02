package br.com.dio.model;

/**
 * Representa uma célula do tabuleiro de Sudoku.
 *
 * <p>Cada célula possui:</p>
 * <ul>
 *   <li>{@code expected} — o valor correto para a solução</li>
 *   <li>{@code actual}   — o valor atual inserido pelo jogador (pode ser nulo)</li>
 *   <li>{@code fixed}    — se verdadeiro, a célula faz parte do puzzle inicial e não pode ser alterada</li>
 * </ul>
 */
public class Space {

    private Integer actual;
    private final int expected;
    private final boolean fixed;

    /**
     * @param expected valor esperado (solução correta) para esta célula
     * @param fixed    se {@code true}, a célula é pré-preenchida e não pode ser alterada pelo jogador
     */
    public Space(final int expected, final boolean fixed) {
        this.expected = expected;
        this.fixed = fixed;
        if (fixed) {
            this.actual = expected;
        }
    }

    /**
     * Define o valor atual da célula. Células fixas ignoram a chamada silenciosamente.
     *
     * @param actual valor a ser inserido (1–9)
     */
    public void setActual(final Integer actual) {
        if (fixed) return;
        this.actual = actual;
    }

    /** Remove o valor atual da célula (equivale a apagar o número). Células fixas são ignoradas. */
    public void clearSpace() {
        setActual(null);
    }

    public Integer getActual() {
        return actual;
    }

    public int getExpected() {
        return expected;
    }

    public boolean isFixed() {
        return fixed;
    }

    @Override
    public String toString() {
        return "Space{actual=" + actual + ", expected=" + expected + ", fixed=" + fixed + '}';
    }
}
