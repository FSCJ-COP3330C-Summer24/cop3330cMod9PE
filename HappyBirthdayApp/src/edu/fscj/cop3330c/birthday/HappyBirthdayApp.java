// HappyBirthdayApp.java
// D. Singletary
// 1/29/23
// wish multiple users a happy birthday

package edu.fscj.cop3330c.birthday;

import edu.fscj.cop3330c.dispatch.Dispatcher;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

// main application class
public class HappyBirthdayApp implements BirthdayCardSender {
    //private User user;
    private ArrayList<User> birthdays = new ArrayList<>();
    // Use a Queue<LinkedList> to act as message queue for the dispatcher
    private Queue<BirthdayCard> queue = new LinkedList<BirthdayCard>();
    private Stream<BirthdayCard> stream = queue.stream();

    private static HappyBirthdayApp hba = new HappyBirthdayApp();

    private HappyBirthdayApp() { }

    public static HappyBirthdayApp getApp() {
        return hba;
    }

    // send the card
    public void sendCard(BirthdayCard bc) {
        Dispatcher<BirthdayCard> d = (c)-> {
            this.queue.add(c);
        };
        d.dispatch(bc);
    }

    // compare current month and day to user's data
    // to see if it is their birthday
    public boolean isBirthday(User u) {
        boolean result = false;

        LocalDate today = LocalDate.now();

        if (today.getMonth() == u.getBirthday().getMonth() &&
                today.getDayOfMonth() == u.getBirthday().getDayOfMonth())
            result = true;

        return result;
    }

    // add multiple birthdays
    public void addBirthdays(User... users) {
        for (User u : users) {
            birthdays.add(u);
        }
    }

    public void processCards() {
        // show the birthdays
        if (!hba.birthdays.isEmpty()) {
            for (User u : hba.birthdays) {
                // see if today is their birthday
                if (hba.isBirthday(u)) {
                    BirthdayCard card;
                    // decorate and send a legacy card
                    if (u instanceof UserWithLocale)
                        card = new BirthdayCard_Localized(new BirthdayCard(u));
                    else
                        card = new BirthdayCard(u);

                    hba.sendCard(card);
                }
            }
        }
    }

    // main program
    public static void main(String[] args) {

        // use current date for testing, adjust where necessary
        ZonedDateTime currentDate = ZonedDateTime.now();

        final User[] USERS = {
            // negative test
            new User("Dianne", "Romero", "Dianne.Romero@email.test",
                currentDate.minusDays(1)),
            // positive tests
            // test with odd length full name
            new User("Sally", "Ride", "Sally.Ride@email.test",
                currentDate),
            // test with even length full name
            new User("René", "Descartes", "René.Descartes@email.test",
                currentDate),
            new User("Johannes", "Brahms", "Johannes.Brahms@email.test",
                currentDate),
            new User("Charles", "Kao", "Charles.Kao@email.test",
                currentDate)
        };

        hba.addBirthdays(USERS);
        hba.processCards();

        hba.birthdays.clear();

        final UserWithLocale[] USERSWITHLOCALES = {
                new UserWithLocale(USERS[0], new Locale("en")),
                new UserWithLocale(USERS[1], new Locale("en")),
                new UserWithLocale(USERS[2], new Locale("fr")),
                new UserWithLocale(USERS[3], new Locale("de")),
                new UserWithLocale(USERS[4], new Locale("zh"))
        };

        hba.addBirthdays(USERSWITHLOCALES);
        hba.processCards();

        // process the stream
        hba.stream.forEach(System.out::print);
    }
}
