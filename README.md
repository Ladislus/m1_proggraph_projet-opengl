##Projet OpenGL ES 
###Thomas Quetier & Ladislas Walcak

------------

####Les shapes
Pour gérer nos formes, nous avons d'abord une interface **Shape.java** 
```java
public interface Shape {
    void draw(float[] scratch);

    Couple<Float> getPosition();
    void setPosition(Couple<Float> position);

    Family getFamily();
    Color getColor();
}

```
Cette classe nous permet notament de facilement gérer les listes de formes dans le GLRenderer. 
Dans un second temps, nous avons la classe Abstraite **DefaultShape.java** qui contient tous le code commun au différente formes (Comme la méthode **draw()**, **setPosition()**, etc).
Enfin, dans chaque classe spécifique (Losange, Square ou Triangle) nous avons les codes propres à chaque famille de formes. Comme la position initiale de chaque sommet de la forme, les indices ainsi que l'initialisation des couleurs. Exemple avec **Losange.java**
```java
package masterimis.proggraphique.opengles.Shapes;

import masterimis.proggraphique.opengles.Utils.Couple;

public class Losange extends DefaultShape {

    private static final float[] LOSANGE_INITIALS = {
            -1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            0.0f, -1.0f, 1.0f,
    };
    private static final short[] LOSANGE_INDICES = { 0, 1, 2, 0, 2, 3};

    public Losange(Couple<Float> position, Color color) {
        super(position, color, Family.LOSANGE, LOSANGE_INITIALS, LOSANGE_INDICES);
    }

    @Override
    protected void initColors(Color color) {
        //Ce code est fait par des professionnels, ne pas le reproduire
        this._colors = new float[] {
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
        };
    }
}
```
####L'affichage initial du Taquin
Maintenant, il est question d'afficher les formes de manière correct, pour créer le taquin initial correspondant à un état final (résolu) du problème. 
Pour cela, nous commençons par ajouter dans la liste des Shapes les élements de notre taquin dans l'ordre : 
```java
        // Ajout des éléments
        shapes.add(new Square(new Couple<>(-1.0f, 1.0f), _colors.get(0)));
        shapes.add(new Square(new Couple<>(-1.0f + 1, 1.0f), _colors.get(1)));
        shapes.add(new Square(new Couple<>(-1.0f + 2, 1.0f), _colors.get(2)));

        shapes.add(new Triangle(new Couple<>(-1.0f, 0.0f), _colors.get(0)));
        shapes.add(new Triangle(new Couple<>(-1.0f + 1, 0.0f), _colors.get(1)));
        shapes.add(new Triangle(new Couple<>(-1.0f + 2, 0.0f), _colors.get(2)));

        shapes.add(new Losange(new Couple<>(-1.0f, -1.0f), _colors.get(0)));
        shapes.add(new Losange(new Couple<>(-1.0f + 1, -1.0f), _colors.get(1)));
```
Avec les Carrés sur la ligne du Haut (donc Y = 1.0f), les Triangles dans la ligne du milieu (Y = 0.0f) et les deux Losanges dans la ligne du bas (Y = -1.0f). Ensuite, dans le** onDrawFrame()**, la liste de Shape et parcouru et tout les élements sont dessinés. 
Nous obtenons donc ce rendu initial : 
[![Image lancement taquin](https://media.discordapp.net/attachments/765538966115319858/843475425626685480/IMG_20210516_150959.jpg?width=301&height=640 "Image lancement taquin")](https://media.discordapp.net/attachments/765538966115319858/843475425626685480/IMG_20210516_150959.jpg?width=301&height=640 "Image lancement taquin")

Pour revenir sur la position des éléments, nous utilisons un couple d'entier X,Y pour les placer. Pour les X on a : -1 a gauche, 0 au milieu & +1 a droite. Pour les Y on a : -1 en bas, 0 au milieu et +1 a droite. Ensuite, les coordonnées sont calculées dans la fonctions setPosition des elements comme suit :
```java
    @Override
    public void setPosition(Couple<Float> position) {
        this._position.setX(position.getX());
        this._position.setY(position.getY());
        for(int i = 0; i < this._coordinates.length; i += 3) {
            this._coordinates[i] = _initials[i] + (OFFSET * position.getX());
            this._coordinates[i + 1] = _initials[i + 1] + (OFFSET * position.getY());
        }
    }
```
L'offset choisis etant dans une variable static (7 sur l'image).

####La gestion des évenements et la classe Plateau 
Maintenant il faut pouvoir jouer ! Pour cela deux choses. 
- D'abord la randomisation du taquin lors du premier "clique"
- Le déplacement de la shape selectionnées, si elle est voisine de la case vide.

Pour pouvoir gérer facilement les actions et conservé l'état actuel de la partie, nous avons créé la classe **Plateau.java**. 

Le taquin est stocké sous la forme d'une liste de liste tel que : 
```java
    private final List<List<Shape>> _plateau = new ArrayList<>();
```

#####Randomisation 
Pour la randomisation, nous partons donc du Taquin résolu et nous effectuons une série de deplacement autorisé pour garantir la résolvabilité du Taquin. 
Voici la fonction randomize() dans la classe Plateau.java :
```java
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
```
Pour avoir un semblant d'animation, nous avons choisi d'actualisé la view dans la fonction, a chaque mouvement. A noté que le nombre de mouvement pour le mélange est une variable static facilement modifiable dans la view.

Aussi, nous vérifions si le taquin mélangé n'est pas final : au quel cas nous remélangeons. 
#####Les actions du joueur
Maintenant le jeu commence ! Nous récupérons d'abord la shape sur laquelle l'utilisateur "clique", de cette façon : 
```java
                float glX = (20.0f * e.getX() / getWidth() - 10.0f) / DefaultShape.OFFSET;
                float glY = (-20.0f * e.getY() / getHeight() + 10.0f) / DefaultShape.OFFSET;

                //Définition des plages de cellules
                boolean XGauche = (glX <= -0.75f && glX >= -1.25f);
                boolean XMilieu = (glX <= 0.25f && glX >= -0.25f);
                boolean XDroite = (glX <= 1.25f && glX >= 0.75f);

                boolean YHaut = (glY <= 0.75f && glY >= 0.25f);
                boolean YMilieu = (glY <= 0.20f && glY >= -0.20f);
                boolean YBas = (glY <= -0.25f && glY >= -0.75f);

                int posX = (XGauche) ? -1 : (XDroite) ? 1 : 0;
                int posY = (YHaut) ? 1 : (YBas) ? - 1 : 0;
                boolean aJouer = this._plateau.play(posX, posY);
```
D'abord, nous récupérons les coordonnées x,y opengl du clique, en les divisant pas l'offset, dans le but de récupérer les coordonnées d'une valeur soit 1, 0 ou - 1 comme expliqué plus haut. Biensur elle ne sont pas exact et donc nous faisons une approximation +- 0.25f pour déduire la position de la shape (posX et PosY). De cette façon, nous pouvons jouer avec la bonne shape.
```java
    public boolean play(int posX, int posY) {
        Couple<Integer> converted = this.glToIndices(posX, posY);

        if(this._voisins.contains(this._plateau.get(converted.getX()).get(converted.getY()))){
            this.swap(converted.getX(), converted.getY(), this._null.getX(), this._null.getY());
            Objects.requireNonNull(this._sounds.get(SOUND_SWIPE)).start();
            this._view.requestRender();
            return true;
        }
        Objects.requireNonNull(this._sounds.get(SOUND_ERROR)).start();
        return false;
    }
```
Ensuite, le plateau prends le relais : on récupere la ligne et la colonne de l'éléments (par rapport a notre structure de données liste de listes). On regarde si la case vide a l'element cliqué comme voisin ( dans la liste this_voisins) et si tel est le cas nous opérons un swap des positions entre ces deux elements. Ensuite on met a jour la view. 
```java
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
```