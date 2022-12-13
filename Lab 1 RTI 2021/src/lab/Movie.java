package lab;

public class Movie {
	public Movie(String[] s) {

		id = s[0];
		name = s[2];
		decade = Integer.parseInt(s[5]) / 10;
		genres = s[8].split(",");
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String[] getGenres() {
		return genres;
	}

	public int getDecade() {
		return decade;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return name + ", with a rating of " + rating + ", with " + numVotes + " votes.";
	}

	public int getNumVotes() {
		return numVotes;
	}

	public void setNumVotes(int numVotes) {
		this.numVotes = numVotes;
	}

	private String name;
	private String id;
	private String[] genres;
	private double rating;
	private int numVotes;
	private int decade;

}
