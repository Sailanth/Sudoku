package br.com.dio.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static br.com.dio.model.GameStatusEnum.COMPLETE;
import static br.com.dio.model.GameStatusEnum.INCOMPLETE;
import static br.com.dio.model.GameStatusEnum.NON_STARTED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Representa o tabuleiro 9×9 de Sudoku.
 *
 * <p>O tabuleiro é armazenado como uma lista de colunas, onde cada coluna
 * é uma lista de {@link Space}s. Ou seja, {@code spaces.get(col).get(row)}
 * retorna a célula na coluna {@code col} e linha {@code row}.</p>
 */
public class Board {

    private final List<List<Space>> spaces;

    /**
     * @param spaces lista de colunas (cada coluna contém 9 {@link Space}s)
     */
    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    /**
     * Retorna uma visão não modificável do tabuleiro.
     * As sublistas internas ainda são mutáveis via métodos de {@link Space},
     * mas a estrutura da lista não pode ser alterada externamente.
     */
    public List<List<Space>> getSpaces() {
        return Collections.unmodifiableList(spaces);
    }

    /**
     * Calcula o status atual do jogo.
     *
     * @return {@link GameStatusEnum#NON_STARTED} se nenhuma célula não-fixa foi preenchida,
     *         {@link GameStatusEnum#INCOMPLETE} se ainda há células vazias,
     *         {@link GameStatusEnum#COMPLETE} se todas as células estão preenchidas.
     */
    public GameStatusEnum getStatus() {
        Stream<Space> allSpaces = flatSpaces();

        boolean anyNonFixedFilled = allSpaces
                .anyMatch(s -> !s.isFixed() && nonNull(s.getActual()));

        if (!anyNonFixedFilled) {
            return NON_STARTED;
        }

        boolean anyEmpty = flatSpaces().anyMatch(s -> isNull(s.getActual()));
        return anyEmpty ? INCOMPLETE : COMPLETE;
    }

    /**
     * Verifica se o tabuleiro contém algum erro (célula preenchida com valor incorreto).
     * Retorna {@code false} se o jogo ainda não foi iniciado.
     */
    public boolean hasErrors() {
        if (getStatus() == NON_STARTED) {
            return false;
        }
        return flatSpaces()
                .anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));
    }

    /**
     * Altera o valor de uma célula não-fixa.
     *
     * @param col   índice da coluna (0–8)
     * @param row   índice da linha  (0–8)
     * @param value valor a inserir   (1–9)
     * @return {@code true} se a alteração foi realizada; {@code false} se a célula é fixa
     */
    public boolean changeValue(final int col, final int row, final int value) {
        var space = spaces.get(col).get(row);
        if (space.isFixed()) {
            return false;
        }
        space.setActual(value);
        return true;
    }

    /**
     * Remove o valor de uma célula não-fixa.
     *
     * @param col índice da coluna (0–8)
     * @param row índice da linha  (0–8)
     * @return {@code true} se a remoção foi realizada; {@code false} se a célula é fixa
     */
    public boolean clearValue(final int col, final int row) {
        var space = spaces.get(col).get(row);
        if (space.isFixed()) {
            return false;
        }
        space.clearSpace();
        return true;
    }

    /**
     * Reinicia o tabuleiro, limpando apenas as células não-fixas.
     * As células fixas (do puzzle original) são preservadas.
     */
    public void reset() {
        flatSpaces()
                .filter(s -> !s.isFixed())
                .forEach(Space::clearSpace);
    }

    /**
     * Verifica se o jogo foi concluído corretamente (sem erros e com todas as células preenchidas).
     */
    public boolean gameIsFinished() {
        return !hasErrors() && getStatus().equals(COMPLETE);
    }

    /** Stream auxiliar que "achata" o tabuleiro em um único stream de {@link Space}s. */
    private Stream<Space> flatSpaces() {
        return spaces.stream().flatMap(Collection::stream);
    }
}
