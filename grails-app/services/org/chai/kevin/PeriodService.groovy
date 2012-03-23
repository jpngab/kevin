package org.chai.kevin


class PeriodService {

	static transactional = true
	
	def sessionFactory
	
	List<Period> getPeriods() {
		List<Period> periods = new ArrayList(Period.list())
		return periods.sort {
			a, b -> a.startDate.compareTo b.startDate
		}
	}
	
}
