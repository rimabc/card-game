
import java.util.*;


public class CardTest {
    public static void main(String[] args) {
        Game myGame = new Game();
        myGame.start();
    }
}

class Game {
    Scanner sc = new Scanner(System.in);
    Deck d = new Deck();
    private int playerCount = 0;
    List<Player> players = new ArrayList<>();


    //게임 여부를 묻는 메서드
    boolean gameStart() {
        System.out.print("게임을 진행하시겠습니까? 진행시 y, 아닐시 n을 입력하여 주세요.>");
        String response = sc.nextLine();
       if (response.equalsIgnoreCase("y")){
           return true;
       }else {
           System.exit(0);
       }return false;
    }

    //게임을 시작하는 메서드
    void start() {
        if (gameStart()) {
            System.out.print("플레이어 수를 입력하세요 (최대 4명):");
            try {
                playerCount = sc.nextInt();
                sc.nextLine();

                if (playerCount < 2 || playerCount > 4) {
                    System.out.println("최소 2명에서 최대 4명까지 가능합니다. 다시 시도해주세요");
                    start();
                }
                for (int i = 0; i < playerCount; i++) {
                    System.out.print("닉네임 입력 (20자 이하): ");
                    String nickname = sc.nextLine();

                    if (nickname.length() > 20) {
                        System.out.println("닉네임이 너무 깁니다. 다시 시도해주세요");
                        players.clear();
                        start();
                    } else {
                        players.add(new Player(nickname));
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("숫자를 입력하세요. 처음부터 시도해주세요.");
                System.exit(0);
            }
        } play(players);
    }

        //100판을 실행하는 메서드
        void play (List < Player > players) {{
            for (int k = 0; k < 100; k++) {
                //카드를 나눠준다
                shareCard(players);
                //최고점수를 가려내기 위한 변수
                int max = 0;
                //한판의 최종 승자를 가려내기 위한 변수
                int winnerIndex = -1;
                //한판의 최고 점수를 뽑는다
                for (int i = 0; i < playerCount; i++) {
                    Player player = players.get(i);
                    player.setValue( rankCheck(player));
                    if (player.getValue()> max) {
                        max = player.getValue();
                        winnerIndex = i;
                    }
                }

                //승자가 있을경우
                if (winnerIndex != -1) {
                    Player winner = players.get(winnerIndex);
                    winner.plusWinScore();
                    winner.pulsGameMoney();

                    //패배들의 1패추가
                    for (int i = 0; i < players.size(); i++) {
                        Player failed = players.get(i);
                        if (!failed.equals(winner)) {
                            failed.setFailScore(+1);
                        }
                    }
                }// 승자가 없을경우 공평하게 10원씩 추가,, 참가비 돌려주는 느낌으로,,
                else {
                    for (int i = 0; i < players.size(); i++) {
                        Player player = players.get(i);
                        player.bonusMoney();
                    }
                }
            }

            Collections.sort(players, new PlayerComparator());

            System.out.println("게임 결과:");
            for (int i = 0; i < players.size(); i++) {
                System.out.println(players.get(i).getNickName()+ ": 점수 = " + players.get(i).getWinScore()+ ", 게임머니 = " + players.get(i).getGameMoney());
            }
        }
    }

    //카드를 나눠주는 메서드
    void shareCard(List<Player> players) {
        d.shuffle();
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < 5; j++) {
                players.get(i).card[j]= d.pick();
            }
        }
    }

    //랭크 체크 해서 점수로 반환해주는 메서드
    int rankCheck(Player p) {
        int[] kind = new int[5];
        int[] number = new int[14];
        boolean flush = false;
        boolean straight = false;
        int stCnt = 0;
        boolean triple = false;
        boolean fourCard = false;
        int pairs = 0;
        //같은 페어에서 또 나누기 위한 변수
        int tierValue = 0;


        for (int i = 0; i < p.card.length; i++) {
            kind[p.card[i].kind]++;
            number[p.card[i].number]++;
        }

        for (int i = 0; i < kind.length; i++) {
            if (kind[i] == 5) {
                flush = true;
                break;
            }
        }


        // pair, triple, four card를 검사한다.
        for (int i = 0; i < number.length; i++) {
            switch (number[i]) {
                case 4:
                    fourCard = true;
                    tierValue = i;
                    break;
                case 3:
                    triple = true;
                    tierValue = i;
                    break;
                case 2:
                    pairs++;
                    break;
            }

            // 스트레이트인지 검사한다.
            if (stCnt == 5) straight = true;

            //"Straight flush"
            if (straight && flush) return 1000 + tierValue;
            //"Four Card"
            if (fourCard) return 900 + tierValue;
            //"Full House"
            if (triple && pairs > 0) return 800 + tierValue;
            //"Flush"
            if (flush) return 700 + tierValue;
            //"Straight"
            if (straight) return 600 + tierValue;
            //"Triple"
            if (triple) return 500 + tierValue;
            //two pairs
            if (pairs == 2) return 400 + tierValue;
            //one pairs
            if (pairs == 1) return 300 + tierValue;

        }
        //no rank
        return 0;
    }
}

class Player {
    private int gameMoney = 10000;
    private int winScore = 0;
    private int failScore = 0;
    private int value = 0;
    private String nickName;
   Card[] card;

    public int getGameMoney() {
        return gameMoney;
    }

    public void pulsGameMoney() {
        this.gameMoney+=100;
    }

    public void bonusMoney() {
        this.gameMoney+=10;
    }

    public void plusWinScore() {
        this.winScore++;
    }
    public int getWinScore() {
        return winScore;
    }

    public int getFailScore() {
        return failScore;
    }

    public void setFailScore(int failScore) {
        this.failScore = failScore;
    }

    public void plusFailScore() {
        this.failScore++;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    Player(String nickName) {
        this.nickName = nickName;
        card = new Card[5];
    }


    public String toString() {
        String temp = "";
        for (int i = 0; i < card.length; i++) {
            temp += card[i] + " ";
        }
        return temp;
    }
}

//내림차순 정렬을 위한
class PlayerComparator implements Comparator<Player> {
    @Override
    public int compare(Player p1, Player p2) {
        // 내림차순 정렬
        return Integer.compare(p2.getWinScore(), p1.getWinScore());
    }
}

class Deck {
    final int CARD_NUM = 52; // 카드의 개수
    Card[] c = new Card[CARD_NUM];

    Deck() { // Deck의 카드를 초기화한다.
        int i = 0;

        for (int k = Card.KIND_MAX; k > 0; k--) {
            for (int n = 1; n < Card.NUM_MAX + 1; n++) {
                c[i++] = new Card(k, n);
            }
        }
    }

    Card pick(int index) { // 지정된 위치(index)에 있는 카드 하나를 선택한다.
        return c[index % CARD_NUM];
    }

    Card pick() { // Deck에서 임의의 위치에 있는 카드 하나를 선택한다.
        int index = (int) (Math.random() * CARD_NUM);
        return pick(index);
    }

    void shuffle() { // 카드의 순서를 섞는다.
        for (int n = 0; n < 1000; n++) {
            int i = (int) (Math.random() * CARD_NUM);
            Card temp = c[0]; // 첫 번째 카드와 임의로 선택된 카드를 서로 바꾼다.
            c[0] = c[i];
            c[i] = temp;
        }
    }
}


class Card{
    static final int KIND_MAX = 4; // 카드 무늬의 수
    static final int NUM_MAX = 13; // 무늬별 카드 수

    static final int SPADE = 4;
    static final int DIAMOND = 3;
    static final int HEART = 2;
    static final int CLOVER = 1;

   int kind;
   int number;

    Card() {
        this(SPADE, 1);
    }

    Card(int kind, int number) {
        this.kind = kind;
        this.number = number;
    }

    public String toString() {
        String kind = "";
        String number = "";

        switch (this.kind) {
            case 4:
                kind = "♤";
                break;
            case 3:
                kind = "◇";
                break;
            case 2:
                kind = "♡";
                break;
            case 1:
                kind = "♧";
                break;
            default:
        }

        switch (this.number) {
            case 13:
                number = "K";
                break;
            case 12:
                number = "Q";
                break;
            case 11:
                number = "J";
                break;
            case 1:
                number = "A";
                break;
            default:
                number = this.number + "";
        }
        return kind + number;
    }
}