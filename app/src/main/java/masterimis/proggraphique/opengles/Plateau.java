package masterimis.proggraphique.opengles;

import android.media.MediaPlayer;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public static final Integer SOUND_SWIPE = 0;
    public static final Integer SOUND_ERROR = 1;
    public static final Integer SOUND_WIN = 2;
    private final Map<Integer, MediaPlayer> _sounds;

    public Plateau(GLRenderer renderer, GLView glView, Map<Integer, MediaPlayer> sounds) {
        this._renderer = renderer;
        this._view = glView;
        this._sounds = sounds;
    }


    /**
     * Methode pour convertir des coordonnées OpenGL en indices
     * @param x coordonnées X
     * @param y coordonnées Y
     * @return Couple d'indices
     */
    private Couple<Integer> glToIndices(float x, float y) {
        int indexY = (int) (x + 1);
        int indexX = (int) (1 - y);

        // Assert les indexes sont valides
        assert indexX >= 0 && indexX <= 2;
        assert indexY >= 0 && indexY <= 2;

        return new Couple<>(indexX, indexY);
    }

    /**
     * Methode raccourcie
     * @param position Couple de position OpenGL
     * @return Couple d'indices
     */
    private Couple<Integer> glToIndices(Couple<Float> position) {
        return this.glToIndices(position.getX(), position.getY());
    }

    /**
     * Méthode pour convertir les indices en coordonnées OpenGL
     * @param x L'indice X
     * @param y L'indice Y
     * @return Un couple de coordonnées
     */
    private Couple<Float> indicesToGL(int x, int y) {
        int glX = -1 + y;
        int glY = 1 - x;

        // Assert les coordonnées sont valides
        assert glX >= -1 && glX <= 1;
        assert glY >= -1 && glY <= 1;

        return new Couple<Float>(0.0f + glX, 0.0f + glY);
    }

    /**
     * Méthode pour actualiser la liste des voisins de la case null
     */
    private void getVoisins() {
        this._voisins.clear();
        // Droite
        if (this._null.getX() <= 1) getShape(this._null.getX() + 1, this._null.getY()).ifPresent(this._voisins::add);
        // Gauche
        if (this._null.getX() >= 1) getShape(this._null.getX() - 1, this._null.getY()).ifPresent(this._voisins::add);
        // Haut
        if (this._null.getY() <= 1) getShape(this._null.getX(), this._null.getY() + 1).ifPresent(this._voisins::add);
        // Bas
        if (this._null.getY() >= 1) getShape(this._null.getX(), this._null.getY() - 1).ifPresent(this._voisins::add);
    }

    /**
     * Méthode pour initialiser le plateau actuel en fonction des Shape OpenGL
     * @param shapes La liste des Shape OpenGL
     */
    private void updateContent(List<Shape> shapes) {
        this._plateau.clear();

        // Transformation de la liste des Shape en matrice
        for (int i = 0; i < SIZE; i++) {
            List<Shape> line = new ArrayList<>();
            line.add(shapes.get(i * SIZE));
            line.add(shapes.get(i * SIZE + 1));
            line.add((i * SIZE + 2 >= 8) ? null : shapes.get(i * SIZE + 2));

            this._plateau.add(line);
        }

        // Actualisation des voisins
        this.getVoisins();
    }


    /**
     * Méthode getter
     * @return Shape en (X, Y)
     */
    private Optional<Shape> getShape(int x, int y) {
        // Vérification que les indices soient valident
        assert (x <= 2 && x >= 0);
        assert (y <= 2 && y >= 0);

        if (Objects.isNull(this._plateau.get(x).get(y))) return Optional.empty();
        return Optional.of(this._plateau.get(x).get(y));
    }

    /**
     * Méthode setter
     * @return La Shape en (X, Y) avant le changement
     */
    private Optional<Shape> setShape(int x, int y, Shape shape) {
        Optional<Shape> previous = this.getShape(x, y);
        this._plateau.get(x).set(y, shape);
        return previous;
    }


    /**
     * Méthode pour swap deux éléments, (x, y) est supposé être le null
     * @param xShape Coordonées X de la shape
     * @param yShape Coordonées Y de la shape
     * @param x Coordonées X du null
     * @param y Coordonées X du null
     */
    private void swap(int xShape, int yShape, int x, int y) {
        // Récupération de la shape
        Optional<Shape> optionalShapeToSwap = this.getShape(xShape, yShape);
        assert optionalShapeToSwap.isPresent();

        Shape shapeToSwap = optionalShapeToSwap.get();
        // Calcul des nouvelle coordonées OpenGL
        Couple<Float> shapeNewPosition = indicesToGL(x, y);
        shapeToSwap.setPosition(shapeNewPosition);

        // Échange des deux places
        this.setShape(x, y, shapeToSwap);
        this.setShape(xShape, yShape, null);

        // Mise à jour de la place du null
        this._null = new Couple<>(xShape, yShape);
        // Actualisation de la liste des voisins
        this.getVoisins();
    }


    /**
     * Méthode pour mélanger le plateau
     * @param rounds Le nombre d'itérations
     */
    public void randomize(int rounds) {

        // Récupération de la matrice de jeu
        this.updateContent(this._renderer.getShapes());

        // Construction matrice réponse
        for (List<Shape> subList : this._plateau) {
            List<Pair<Family, Color>> line = new ArrayList<>();
            for (Shape shape : subList) line.add(Objects.isNull(shape) ? null : new Pair<>(shape.getFamily(), shape.getColor()));
            this._correct.add(line);
        }

        // Itérations
        for (int i = 0; i < rounds; i++) {
            // Sélection d'un voisin aléatoire
            int randomIndex = _random.nextInt(this._voisins.size());
            Shape shapeToSwap = this._voisins.get(randomIndex);
            // Position dans le plateau
            Couple<Integer> indices = this.glToIndices(shapeToSwap.getPosition());

            // Échange des deux éléments
            this.swap(indices.getX(), indices.getY(), this._null.getX(), this._null.getY());
            // Mise à jour de l'affichage
            this._view.requestRender();
            try {
                Thread.sleep(SWAP_SPEED);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode pour vérifier que le plateau est fini
     * @return True si le plateau est correct, False sinon
     */
    public boolean check() {
        assert this._correct.size() > 0;

        // Itération sur toute les cases, et vérification que la forme et la couleur sont égaux
        for (int line = 0; line < this._plateau.size(); line++)
            for (int column = 0; column < this._plateau.get(line).size(); column++) {
                Shape shape = this._plateau.get(line).get(column);
                Pair<Family, Color> correct = this._correct.get(line).get(column);

                if ((Objects.isNull(shape) && !Objects.isNull(correct)) || (!Objects.isNull(shape) && Objects.isNull(correct))) return false;
                if (!Objects.isNull(shape) && (!shape.getColor().equals(correct.second) || !shape.getFamily().equals(correct.first))) return false;
            }
        // Si tout est bon, son
        Objects.requireNonNull(this._sounds.get(SOUND_WIN)).start();
        return true;
    }

    /**
     * Méthod pour jouer un coup
     * @param posX Indexe X
     * @param posY Indexe Y
     * @return True si un coup a été joué, False si le coup est invalide
     */
    public boolean play(int posX, int posY) {
        // Récupération des indexes
        Couple<Integer> converted = this.glToIndices(posX, posY);

        // Si la pièce touchée est dans les voisins du null (i.e il est déplaceable)
        if(this._voisins.contains(this._plateau.get(converted.getX()).get(converted.getY()))) {
            // Échange des deux pièces + son
            this.swap(converted.getX(), converted.getY(), this._null.getX(), this._null.getY());
            Objects.requireNonNull(this._sounds.get(SOUND_SWIPE)).start();

            // Mise à jour de la vue
            this._view.requestRender();
            return true;
        }

        // Si la pièce n'est pas un voisin, le coup est invalide
        Objects.requireNonNull(this._sounds.get(SOUND_ERROR)).start();
        return false;
    }
}
