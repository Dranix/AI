package Tank;

import java.util.Comparator;

public class EnergyComparator implements Comparator<EnemyTank> {
	@Override
	public int compare(EnemyTank o1, EnemyTank o2) {
		if (o1.get_energy() > o2.get_energy()) {
			return 1;
		} else if (o1.get_energy() == o2.get_energy()) {
			return 0;
		}

		return -1;
	}
}
