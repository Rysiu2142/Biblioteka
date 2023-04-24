package main.model.Dao;

import main.model.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 *  Klasa sluzy do polaczenia z baza, otwierania polaczen, commitowania zmian.
 *
 */
public  class HibernateUtil {

    private Session currentSession;
    private Transaction currentTransaction;
    private static ServiceRegistry serviceRegistry;
    private static String mainConfiguration;
    private static SessionFactory sessionFactory;


    public static void OpenConnection(String configurationPath){
        mainConfiguration = configurationPath;
        setSessionFactory(mainConfiguration);
    }

    public static void CloseConnection(){
        sessionFactory.close();
        StandardServiceRegistryBuilder.destroy(serviceRegistry);

    }

    public static String getMainConfiguration(){
        return mainConfiguration;
    }

    public static ServiceRegistry getServiceRegistry() {
        return HibernateUtil.serviceRegistry;
    }

    /**
     * Metoda twarzaca session factory
     * @param pathConfiguration sciezka do pliku konfiguracyjnego
     */
    public static void setSessionFactory(String pathConfiguration) {
        Configuration configuration = new Configuration()
                .configure(pathConfiguration);
        configuration.addAnnotatedClass(AuthEntity.class);
        configuration.addAnnotatedClass(AuthorEntity.class);
        configuration.addAnnotatedClass(BooksauthorEntity.class);
        configuration.addAnnotatedClass(BooksEntity.class);
        configuration.addAnnotatedClass(BookstitleEntity.class);

        configuration.addAnnotatedClass(OrdersEntity.class);
        configuration.addAnnotatedClass(SugestionsEntity.class);
        configuration.addAnnotatedClass(UsersEntity.class);

        serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();

        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

    }

    /**
     * Metoda otwierajaca nowa sesje
     * @return zwraca otwarta sesje
     */
    public Session openCurrentSession() {
        currentSession = sessionFactory.openSession();
        return currentSession;
    }

    /**
     * Metoda otwierajaa sesje wraz z tranzakcja
     * @return zwraca otwarta sesje
     */
    public Session openCurrentSessionwithTransaction() {
        currentSession = sessionFactory.openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    /**
     * Zamyka obecna sesje
     */
    public void closeCurrentSession() {
        currentSession.close();
    }

    /**
     * Zamyka sesje wraz z tranzakcaj
     */
    public void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
    }

    /**
     * Pobiera obecna sesje
     * @return zwraca sesje
     */
    public Session getCurrentSession() {
        return currentSession;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @SuppressWarnings("deprecation")
    private static SessionFactory buildSessionFactory()
    {
        try {
            return new Configuration().configure().buildSessionFactory();
        }catch(Throwable ex)
        {
            System.err.println("SessionFactroy Failed."+ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

}
