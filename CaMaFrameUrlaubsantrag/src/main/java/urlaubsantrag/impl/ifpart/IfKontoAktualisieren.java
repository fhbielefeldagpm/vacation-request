package urlaubsantrag.impl.ifpart;

import cm.core.data.CaseFileItem;
import cm.core.data.SimpleProperty;
import cm.core.sentries.IfPart;
import cm.core.sentries.IfPartImplementation;

public class IfKontoAktualisieren extends IfPartImplementation {

	public IfKontoAktualisieren(IfPart ip) {
		super(ip);
	}

	@Override
	public boolean isSatisfied() {
		boolean response = false;
		CaseFileItem urlaubsantrag = ip.getCaseFileItemRef();
		SimpleProperty genehmigt = urlaubsantrag.getProperty("genehmigt");
		if(genehmigt != null) {
			response = Boolean.parseBoolean(genehmigt.getValue());
		}		
		return response;
	}

}
