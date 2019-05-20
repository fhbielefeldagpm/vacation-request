/*
 * Copyright © 2018-2019 André Zensen, University of Applied Sciences Bielefeld
 * and various authors (see https://www.fh-bielefeld.de/wug/forschung/ag-pm)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package urlaubsantrag.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import cm.core.utils.PersistenceSettings;
import urlaubsantrag.model.Urlaubskonto;
/**
 * <p>
 * Simple service to retrieve and update an employee's holiday account ({@link Urlaubskonto}).
 * </p>
 * 
 * @author André Zensen
 *
 */
@Stateless
public class UrlaubskontoService {

	@PersistenceContext(unitName = PersistenceSettings.persistenceContextUnitName)
	EntityManager em;
	
	public UrlaubskontoService() {
		
	}
	/**
	 * Method to get an employee's holiday account (Urlaubskonto)
	 * @param antragstellerId the employee's persistent ID
	 * @return
	 */
	public Urlaubskonto getUrlaubskonto(long antragstellerId) {
		TypedQuery<Urlaubskonto> query = em.createQuery("SELECT u FROM Urlaubskonto u WHERE u.antragstellerId = :id", Urlaubskonto.class);
		query.setParameter("id", antragstellerId);
		Urlaubskonto fetched = query.getSingleResult();
		return fetched;
	}
	/**
	 * Method to substract requested days from an employee's holiday account.
	 * @param antragstellerId persistent ID of an employee
	 * @param tage days to substract from the employee's account
	 */
	public void substractTage(long antragstellerId, int tage) {
		TypedQuery<Urlaubskonto> query = em.createQuery("SELECT u FROM Urlaubskonto u WHERE u.antragstellerId = :id", Urlaubskonto.class);
		query.setParameter("id", antragstellerId);
		Urlaubskonto fetched = query.getSingleResult();
		fetched.setUrlaubstage(fetched.getUrlaubstage() - tage);
	}
	
}
