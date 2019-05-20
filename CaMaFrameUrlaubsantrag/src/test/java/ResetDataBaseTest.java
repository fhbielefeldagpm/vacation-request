
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.Test;

//import org.junit.Test;

import cm.core.CaseRole;
import cm.core.CaseWorker;
import cm.core.utils.CaseFactory;
import cm.core.utils.PersistenceSettings;
import urlaubsantrag.model.Urlaubskonto;

/**
 * <p>
 * Class used to generate the database schema via JPA entity manager. Uses a
 * local persistence unit. After generating the table structure, two CaseWorkers
 * with roles derived from the {@link CaseFactory} class are persisted.
 * </p>
 * 
 * @author André Zensen
 *
 */
public class ResetDataBaseTest {

	@Test
	public void rebuildDropAndCreate() {
		
		// create table structure
		Map<String, String> persistenceMap = new HashMap<String, String>();
		persistenceMap.put("eclipselink.ddl-generation", "drop-and-create-tables");
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory(PersistenceSettings.persistenceContextUnitLocalName, persistenceMap);
		// create and persist CaseRoles and CaseWorkers
		EntityManager em = emf.createEntityManager();

		persistenceMap = new HashMap<String, String>();
		persistenceMap.put("eclipselink.ddl-generation", "none");
		emf = Persistence.createEntityManagerFactory(PersistenceSettings.persistenceContextUnitLocalName,
				persistenceMap);
		em = emf.createEntityManager();

		CaseRole bearbeiter = new CaseRole(CaseFactory.CaseRoles.bearbeiter);
		CaseRole pruefer = new CaseRole(CaseFactory.CaseRoles.pruefer);

		em.getTransaction().begin();
		em.persist(bearbeiter);
		em.persist(pruefer);
		em.getTransaction().commit();

		CaseWorker admin = new CaseWorker("admin", "admin", "John", "Admin", true);
		admin.addCaseRole(pruefer);
		CaseWorker worker = new CaseWorker("worker", "worker", "Jane", "Worker", false);
		worker.addCaseRole(bearbeiter);

		TypedQuery<CaseWorker> query = em.createQuery("SELECT cw FROM CaseWorker cw", CaseWorker.class);
		List<CaseWorker> caseWorkers = query.getResultList();

		List<String> foundNames = new ArrayList<>();

		if (caseWorkers.size() > 0) {
			for (CaseWorker cw : caseWorkers) {
				foundNames.add(cw.getUser());
			}
		}

		try {
			em.getTransaction().begin();
			if (!foundNames.contains("admin")) {
				em.persist(admin);
			}
			if (!foundNames.contains("worker")) {
				em.persist(worker);
			}
			em.getTransaction().commit();
			// create holiday accounts with IDs of persisted CaseWorkers
			em.getTransaction().begin();
			Urlaubskonto adminUrlaubskonto = new Urlaubskonto(admin.getId(), 30);
			em.persist(adminUrlaubskonto);
			Urlaubskonto workerUrlaubskonto = new Urlaubskonto(worker.getId(), 25);
			em.persist(workerUrlaubskonto);
			em.getTransaction().commit();

			em.close();
			emf.close();
		} catch (Error e) {

		}
	}
}
