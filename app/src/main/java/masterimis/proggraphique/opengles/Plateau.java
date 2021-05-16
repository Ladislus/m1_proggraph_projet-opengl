package masterimis.proggraphique.opengles;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import masterimis.proggraphique.opengles.Shapes.Color;
import masterimis.proggraphique.opengles.Shapes.Family;
import masterimis.proggraphique.opengles.Shapes.Shape;
import masterimis.proggraphique.opengles.Utils.Couple;

public class Plateau {

    private static final short SIZE = 3;
    private static final long SWAP_SPEED = 50;

    private static final Random _random = new Random();

    private final List<List<Shape>> _plateau = new ArrayList<>();
    private final List<List<Pair<Family, Color>>> _correct = new ArrayList<>();
    private final List<Shape> _voisins = new ArrayList<>();

    private Couple<Integer> _null = new Couple<>(2, 2);

    private final GLRenderer _renderer;
    private final GLView _view;

    public Plateau(GLRenderer renderer, GLView glView) {
        this._renderer = renderer;
        this._view = glView;
    }

    private Couple<Integer> glToIndices(float x, float y) {
        int indexX = (int) (x + 1);
        int indexY = (int) (1 - y);

        assert indexX >= 0 && indexX <= 2;
        assert indexY >= 0 && indexY <= 2;

        return new Couple<>(indexX, indexY);
    }

    private Couple<Integer> glToIndices(Couple<Float> position) {
        return this.glToIndices(position.getX(), position.getY());
    }

    private Couple<Float> indicesToGL(int x, int y) {
        int glX = x - 1;
        int glY = 1 - y;

        assert glX >= -1 && glX <= 1;
        assert glY >= -1 && glY <= 1;

        return new Couple<Float>(0.0f + glX, 0.0f + glY);
    }

    private void getVoisins() {
        this._voisins.clear();

        if (this._null.getX() <= 1) getShape(this._null.getX() + 1, this._null.getY()).ifPresent(this._voisins::add);
        if (this._null.getX() >= 1) getShape(this._null.getX() - 1, this._null.getY()).ifPresent(this._voisins::add);
        if (this._null.getY() <= 1) getShape(this._null.getX(), this._null.getY() + 1).ifPresent(this._voisins::add);
        if (this._null.getY() >= 1) getShape(this._null.getX(), this._null.getY() - 1).ifPresent(this._voisins::add);
    }

    private void updateContent(List<Shape> shapes) {
        this._plateau.clear();

        for (int i = 0; i < SIZE; i++) {
            List<Shape> line = new ArrayList<>();
            line.add(shapes.get(i * SIZE));
            line.add(shapes.get(i * SIZE + 1));
            line.add((i * SIZE + 2 >= 8) ? null : shapes.get(i * SIZE + 2));

            this._plateau.add(line);
        }
        this.getVoisins();
    }

    private Optional<Shape> getShape(int x, int y) {
        assert (x <= 2 && x >= 0);
        assert (y <= 2 && y >= 0);

        if (Objects.isNull(this._plateau.get(y).get(x))) return Optional.empty();
        return Optional.of(this._plateau.get(y).get(x));
    }

    private Optional<Shape> setShape(int x, int y, Shape shape) {
        Optional<Shape> previous = this.getShape(x, y);
        this._plateau.get(y).set(x, shape);
        return previous;
    }

    private void swap(int xShape, int yShape, int x, int y) {
        Optional<Shape> optionalShapeToSwap = this.getShape(xShape, yShape);
        assert optionalShapeToSwap.isPresent();

        Shape shapeToSwap = optionalShapeToSwap.get();
        Couple<Float> shapeNewPosition = indicesToGL(x, y);
        shapeToSwap.setPosition(shapeNewPosition);

        this.setShape(x, y, shapeToSwap);
        this.setShape(xShape, yShape, null);

        this._null = new Couple<>(xShape, yShape);
        this.getVoisins();
    }

    public void randomize(int rounds) {

        // Récupération de la matrice de jeu
        this.updateContent(this._renderer.getShapes());

        // Construction matrice attendue
        for (List<Shape> subList : this._plateau) {
            List<Pair<Family, Color>> line = new ArrayList<>();
            for (Shape shape : subList) line.add(Objects.isNull(shape) ? null : new Pair<>(shape.getFamily(), shape.getColor()));
            this._correct.add(line);
        }

        for (int i = 0; i < rounds; i++){
            int randomIndex = _random.nextInt(this._voisins.size());

            Shape shapeToSwap = this._voisins.get(randomIndex);
            Couple<Integer> indices = this.glToIndices(shapeToSwap.getPosition());

            this.swap(indices.getX(), indices.getY(), this._null.getX(), this._null.getY());
            this._view.requestRender();
            try {
                Thread.sleep(SWAP_SPEED);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean check() {
        assert this._correct.size() > 0;

        for (int line = 0; line < this._plateau.size(); line++)
            for (int column = 0; column < this._plateau.get(line).size(); column++) {
                Shape shape = this._plateau.get(line).get(column);
                Pair<Family, Color> correct = this._correct.get(line).get(column);

                if ((Objects.isNull(shape) && !Objects.isNull(correct)) || (!Objects.isNull(shape) && Objects.isNull(correct))) return false;
                if (!shape.getColor().equals(correct.second) || !shape.getFamily().equals(correct.first)) return false;
            }
        return true;
    }
}
