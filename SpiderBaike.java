import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SpiderBaike {
	
	String movie = null;
	
	String url = null;
	String baikeUrl = null;
	String doubanUrl = null;
	String m1905Url = null;
	String mtimeUrl = null;
	
	String content = null;
	String doubanContent = null;
	String m1905Content = null;
	String mtimeContent = null;
	String mtimeActorContent = null;
	
	Introduction introduction; //介绍
	Information information; //内容
	MainTheme mainTheme ; //主题
	Manufacture manufacture; //制作
	List<Role> roleList; //角色
	
	public SpiderBaike(String movie, String decode){
		this.movie = movie;
		this.url = "http://baike.baidu.com/item/"+movie;
		this.content = getContent(url, decode);
		this.doubanContent = this.getDoubanMovie(movie);
		this.m1905Content = this.getM1905Movie(movie);
		
		this.introduction = this.getIntroduction();
		this.information = this.getInformation();
		this.mainTheme = this.getMainTheme();
		this.mtimeContent = this.getMtimeMovie();
		this.manufacture = this.getManfacture();
		this.roleList = this.getRoleList();
	}
	
	public static void main(String[] agrs){
		String movie = "疯狂动物城";
		String decode = "utf-8";
		SpiderBaike sb = new SpiderBaike(movie, decode);
		System.out.println("电影:" + movie);
		System.out.println("--------------------------------------------------------------------");
		System.out.println("基本介绍: ");
		System.out.println("电影评分: "+ sb.introduction.score);
		System.out.println("电影票房: " + sb.introduction.boxOffice);
		System.out.println("上映时间: " + sb.introduction.release_time);
		System.out.println("国家地区: " + sb.introduction.area);
		System.out.println("片长: " + sb.introduction.length);
		System.out.println("图片: " + sb.introduction.imgUrl);
		List<String> keywords = sb.introduction.keywords;
		System.out.println("关键词列表: ");
		for(String keyword: keywords){
			System.out.print(keyword + " ");
		}
		System.out.println();
		System.out.println();
		System.out.println("--------------------------------------------------------------------");
		System.out.println("内容:");
		System.out.println("剧情解析:"+ sb.information.story);
		System.out.println("豆瓣评论url:"+ sb.information.doubanUrl);
		System.out.println("影评评价:"+ sb.information.comment);
		System.out.println("获奖情况: ");
		List<String> prizes = sb.information.prize;
		for(String one: prizes){
			System.out.println(one);
		}
		
		System.out.println();
		System.out.println();
		System.out.println("--------------------------------------------------------------------");
		System.out.println("主题:");
		System.out.println("风格:" + sb.mainTheme.style);
		System.out.println("题材内容:" + sb.mainTheme.theme);
		System.out.println("相关电影:");
		for(String smovie : sb.mainTheme.similarMovie){
			System.out.print(smovie + " ");
		}
		
		System.out.println();
		System.out.println();
		System.out.println("--------------------------------------------------------------------");
		System.out.println("制作:");
		System.out.println("出品公司");
		for(Company company: sb.manufacture.company){
			System.out.println("名称:" + company.cname);
			System.out.print("未来作品:");
			for(String  represent : company.futureRepresent){
				System.out.print(represent + " ");
			}
			System.out.println();
			System.out.print("过去作品:");
			for(String represent: company.pastRepresent){
				System.out.print(represent + " ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("导演:");
		for(Director director: sb.manufacture.directors){
			System.out.println("名称:" + director.dname);
			System.out.println("图片:" + director.imgurl);
			System.out.println("评分:" + director.rate);
			System.out.print("代表作品:");
			for(String rp : director.represent){
				System.out.print(rp + " ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("编剧:");
		for(ScriptWriter scriptwriter: sb.manufacture.scriptwriters){
			System.out.println("名称:" + scriptwriter.sname);
			System.out.print("代表作品:");
			for(String rp : scriptwriter.represent){
				System.out.print(rp + " ");
			}
			System.out.println();
		}
		
		System.out.println();
		System.out.println();
		System.out.println("--------------------------------------------------------------------");
		System.out.println("角色:");
		for(Role role : sb.roleList){
			System.out.println("角色名:" + role.name);
			System.out.println("角色图片:" + role.imgurl);
			System.out.println("角色介绍:" + role.name);
			System.out.println("对应的演员:" + role.actor.name);
			System.out.println("演员出生日期:" + role.actor.birthday);
			System.out.println("演员国籍:" + role.actor.country);
			System.out.println("演员图片:" + role.actor.imgurl);
			System.out.print("演员代表作品:");
			for(String rp: role.actor.represent){
				System.out.print(rp + " ");
			}
			System.out.println();
		}
	}
	public static String getContent(String url, String decode) {
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		String content = "";
		BufferedReader in = null;
		try{
				URL realUrl = new URL(url);
				URLConnection connection = realUrl.openConnection();
	//			connection.setRequestProperty("Accept", "*/*");
	//			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
	//			connection.setRequestProperty("Connection", "keep-alive");
	//			connection.setDoOutput(true); //post
	//			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:49.0) Gecko/20100101 Firefox/49.0");
	//			connection.setDoInput(true);  //post
	//			connection.connect();
				HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
				httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:49.0) Gecko/20100101 Firefox/49.0");
				
				in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), decode));
				String line;
				while((line = in.readLine()) != null){
					content += line;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			try {
				if( in != null){
					in.close();
				}
			}catch(Exception e2){
				e2.printStackTrace();
			}
		}
		content = content.replace("&nbsp", "");
		return content;
	}
	
	public  String getDoubanMovie(String movie){
		String dbSearchUrl = "https://movie.douban.com/subject_search?search_text=" +  movie;
		String searchContent = getContent(dbSearchUrl, "utf-8");
		String regexdbMovie = " <a class=\"nbg\" href=\"(.*?)\" onclick=\".*?\" title=\""+movie+"\">";
		Pattern pattern = Pattern.compile(regexdbMovie);
		Matcher matcher = pattern.matcher(searchContent);
		String dbMovieUrl = "";
		if(matcher.find()){
			dbMovieUrl = matcher.group(1);
		}
		this.doubanUrl = dbMovieUrl;
		String movieContent = getContent(dbMovieUrl, "utf-8");
		return movieContent;
	}
	
	public String getM1905Movie(String movie){
		String m1905Url = "http://www.1905.com/search/?q=" + movie;
		String searchContent = getContent(m1905Url, "utf-8");
		String regexM1905Movie = "<div class=\"movie-pic\">.*?<a href=\"(.*?)\" target=\"_blank\" class=\"img-a\"><img width=.*?alt=\""+movie+"\" title=\""+movie+"\"></a>";
		Pattern pattern = Pattern.compile(regexM1905Movie);
		Matcher matcher = pattern.matcher(searchContent);
		String m1905MovieUrl = "123";
		if(matcher.find()){
			m1905MovieUrl = matcher.group(1);
		}
		this.m1905Url = m1905MovieUrl;
		String movieContent = getContent(m1905MovieUrl, "utf-8");
		return movieContent;
	}
	
	public String getMtimeMovie(){
		String movie = this.movie;
		String mtimeUrl = "http://service.channel.mtime.com/Search.api?Ajax_CallBack=true&Ajax_CallBackType=Mtime.Channel.Services&Ajax_CallBackMethod=GetSearchResult&Ajax_CrossDomain=1&Ajax_RequestUrl=http://search.mtime.com/search/?q="+movie+"&t=1&t=2016113021433167701&Ajax_CallBackArgument0="+movie+"&Ajax_CallBackArgument1=1&Ajax_CallBackArgument2=290&Ajax_CallBackArgument3=0&Ajax_CallBackArgument4=1";
		String searchContent = getContent(mtimeUrl, "utf-8");
		String regexId = "\"movieId\":(.*?),\"movieTitle\"";
		Pattern pattern = Pattern.compile(regexId);
		Matcher matcher = pattern.matcher(searchContent);
		String id = "";
		if(matcher.find()){
			id = matcher.group(1);
		}
		this.mtimeUrl = "http://movie.mtime.com/" + id ;
		
		String mtimeContent = getContent(this.mtimeUrl, "utf-8");
		return mtimeContent;
	}
	
	public  Introduction getIntroduction(){
		String score = this.getScore();  //获取电影评分
		String boxOffice = this.getBoxOffice(); //获取票房成绩
		String release_time = this.getRelease_time(); //获取上映时间
		String area = this.getArea();//获取地区
		String length = this.getLength(); //获取片长
		List<String> keywords = this.getKeyword(); //获取关键词列表
		String imgurl =getImgurl(); //获取电影图片
		Introduction introduction = new Introduction(score, boxOffice, release_time,area, length, keywords, imgurl);
		return introduction;
	}
	

	public Information getInformation(){
		String story = this.getStory();
		String  comment = this.getComment();
		String doubanUrl = this.doubanUrl;
		List<String> prize = this.getPrize();
		Information information = new Information(story, comment, doubanUrl, prize);
		return information;
	}

	public MainTheme getMainTheme(){
		String style = this.getStyleStr(); //获取风格
		String theme = this.getThemeStr();//获取主题
		List<String> similarMovies = this.getSimilarMovie();//获取相关电影
		MainTheme mainTheme  = new MainTheme(style, theme, similarMovies);
		return mainTheme;
	}
	
	public Manufacture getManfacture(){
		List<Company> company = this.getCompanyList() ;
		List<Director> directors = this.getDirectorList();
		List<ScriptWriter> scriptwriters = this.getSriptWriterList();
		Manufacture manufacture = new Manufacture(company, directors, scriptwriters);
		return manufacture;
	}
	
	public String getDescription(){
		String regexIntro = "<meta name=\"description\" content=\"(.*?)\">";
		Pattern pattern = Pattern.compile(regexIntro);
		Matcher matcher = pattern.matcher(this.content);
		String description = "";
		while(matcher.find()){
			description += matcher.group(1);
		}
		return description;
	}
	
	public List<String> getKeyword(){
		String regexKeywords = "<li class=\"bdEB pdl20 pdr20 ellipsis\">基因(.*?)</li>";
		String keyWords = "";
		Pattern pattern = Pattern.compile(regexKeywords);
		Matcher matcher = pattern.matcher(this.m1905Content);
		if(matcher.find()){
			keyWords= matcher.group(1);
		}
		keyWords = keyWords.replace("</a>", "`");
		keyWords = keyWords.replaceAll("<.*?>", "");
		keyWords = keyWords.replace(" ", "");
		keyWords = keyWords.replace("：", "");
		List<String> keyWordList = new ArrayList<String>();
		String[] keys = keyWords.split("`");
		for(String word: keys){
			keyWordList.add(word);
		}
		return keyWordList;
	}
	
	public String getBoxOffice(){
		String regexboxOffice = "</li>.*?<li>(.*?)—内地票房</li>";
		Pattern pattern = Pattern.compile(regexboxOffice);
		Matcher matcher = pattern.matcher(this.m1905Content);
		String boxOffice = "";
		if(matcher.find()){
			boxOffice = matcher.group(1);
		}
		return boxOffice;
	}
	public String getScore(){
		String regexScore = " <strong class=\"ll rating_num\" property=\"v:average\">(.*?)</strong> ";
		Pattern pattern = Pattern.compile(regexScore);
		Matcher matcher = pattern.matcher(this.doubanContent);
		String score = "";
		if(matcher.find()){
			score = matcher.group(1);
		}
		return score;
	}
	
	public String getRelease_time(){
		String regexRealeaseTime = "<span class=\"pl\">上映日期:</span> <span property=\"v:initialReleaseDate\" content=\"(.*?)\">";
		Pattern pattern = Pattern.compile(regexRealeaseTime);
		Matcher matcher = pattern.matcher(this.doubanContent);
		String release_time = "";
		if(matcher.find()){
			release_time = matcher.group(1);
		}
		return release_time;
	}
	
	public String getArea(){
		String regexArea = "<span class=\"pl\">制片国家/地区:</span>(.*?)<br/>";
		Pattern pattern = Pattern.compile(regexArea);
		Matcher matcher = pattern.matcher(doubanContent);
		String area = "";
		if(matcher.find()){
			area = matcher.group(1);
		}
		return area;
	}
	
	public String getLength(){
		String regexLength = "<span class=\"pl\">片长:</span> <span property=\"v:runtime\" content=\".*?\">(.*?)</span>";
		Pattern pattern = Pattern.compile(regexLength);
		Matcher matcher = pattern.matcher(doubanContent);
		String length = "";
		if(matcher.find()){
			length = matcher.group(1);
		}
		return length;
	}
	public String getImgurl(){
		String regexImgurl = "<li data-mediaId=\".*?\"><a.*?><img src=\"(.*?)\" /><span class=\"video-shadow\"></span>";
		Pattern pattern = Pattern.compile(regexImgurl);
		Matcher matcher = pattern.matcher(content);
		String imgurl = "";
		if(matcher.find()){
			imgurl = matcher.group(1);
		}
		return imgurl;
	}
	
	public String getStory(){
		String regexPlot = "<h2 class=\"title-text\".*?剧情简介</h2>.*?<div class=\"para\" label-module=\"para\">(.*?)</div><div class=\"anchor-list\">";
		Pattern pattern = Pattern.compile(regexPlot);
		content = content.replace("\n", "");
		Matcher matcher = pattern.matcher(content);
		String story = "";
		if(matcher.find()){
			story = matcher.group(1);
		}
		story = story.replaceAll("<.*?>", "");
		return story;
	}
	
	public String getComment(){
		String regexComment = "<a name=\"影片评价\" class=\"lemma-anchor \" ></a>(.*?)<span class=\"title-text\">电影评价</span>";
		Pattern pattern = Pattern.compile(regexComment);
		Matcher matcher = pattern.matcher(content);
		String commentStr = "";
		if(matcher.find()){
			commentStr = matcher.group(1);
		}
		commentStr = commentStr.replaceAll("<.*?>", "");
		return commentStr;
	}
	
	public List<String> getPrize(){
		String regexPrize = " <ul class=\"award\">.*?<li>.*?<a href=\".*?\">(.*?)</a>.*?</li>.*?<li>(.*?)</li>.*?<li>";
		Pattern pattern = Pattern.compile(regexPrize);
		Matcher matcher = pattern.matcher(this.doubanContent);
		List<String> prize = new ArrayList<String>();
		while(matcher.find()){
			String one = "";
			one += matcher.group(1) + " " + matcher.group(2);
			prize.add(one);
		}
		return prize;
	}
	public String getStyleStr(){
		String regexStyle = "<span property=\"v:genre\">(.*?)</span>";
		Pattern pattern = Pattern.compile(regexStyle);
		Matcher matcher = pattern.matcher(this.doubanContent);
		String styleStr = "";
		while(matcher.find()){
			styleStr += matcher.group(1) + " ";
		}
		return styleStr;
	}
	
	public String getThemeStr(){
		String regexTheme = "<div class=\"lemmaWgt-lemmaSummary lemmaWgt-lemmaSummary-light\">.*?该片讲述了(.*?)该片于";
		Pattern pattern = Pattern.compile(regexTheme);
		Matcher matcher = pattern.matcher(this.content);
		String themeStr = "";
		if(matcher.find()){
			themeStr = matcher.group(1);
		}
		return themeStr;
	}
	
	public List<String> getSimilarMovie(){
		String regexSimilarMovieBlock = " <div class=\"recommendations-bd\">(.*?)</div>";
		Pattern pattern = Pattern.compile(regexSimilarMovieBlock);
		Matcher matcher = pattern.matcher(this.doubanContent);
		String block = "";
		if(matcher.find()){
			block = matcher.group(1);
		}
		String regexSimilarMovie = "<a href=\".*?\" class=\"\" >(.*?)</a>";
		pattern = Pattern.compile(regexSimilarMovie);
		matcher = pattern.matcher(block);
		List<String> similarMovie = new ArrayList<String>();
		while(matcher.find()){
			similarMovie.add(matcher.group(1));
		}
		return similarMovie;
	}
	
	public List<Company> getCompanyList(){
		String url = this.mtimeUrl + "/details.html#company";
		String companyContent = getContent(url, "utf-8");
		String regexCompanyBlock = "<h4>制作公司</h4>.*?<ul>(.*?)</ul>.*?<h4>发行公司</h4>";
		Pattern pattern = Pattern.compile(regexCompanyBlock);
		Matcher matcher = pattern.matcher(companyContent);
		String block = "";
		if(matcher.find()){
			block = matcher.group(1);
		}
		String regexCompany = "<li>.*?<a href=\"(.*?)\" target=\"_blank\">(.*?)</a>.*?</li> ";
		pattern = Pattern.compile(regexCompany);
		matcher = pattern.matcher(block);
		List<Company> companyList = new ArrayList<Company>();
		while(matcher.find()){
			String companyUrl = matcher.group(1);
			String cname = matcher.group(2).replace("&#183;", "");
			Company company = this.getCompany(companyUrl, cname);
			companyList.add(company);
		}
		return companyList;
	}
	
	
	public List<Director> getDirectorList(){
		String actorUrl = this.mtimeUrl + "/fullcredits.html";
		this.mtimeActorContent = getContent(actorUrl, "utf-8" );
		String regexDirectorblock = "<h4>导演 Director</h4>(.*?)<h4>编剧 Writer</h4>";
		Pattern pattern = Pattern.compile(regexDirectorblock);
		Matcher matcher = pattern.matcher(this.mtimeActorContent);
		String block = "";
		if(matcher.find()){
			block = matcher.group(1);
		}
		String regexDirectorList = "<a title=\"(.*?)\" target=\"_blank\" href=\"(.*?)\">.*?<img.*?src=\"(.*?)\" /></a>";
		pattern = Pattern.compile(regexDirectorList);
		matcher = pattern.matcher(block);
		List<Director> directorList = new ArrayList<Director>();
		while(matcher.find()){
			String dname = matcher.group(1);
			String directorurl = matcher.group(2);
			String imgurl = matcher.group(3);
		    People people = this.getPeople(directorurl);
		    List<String> represent = people.represent;
		    String rate = people.rate;
		    Director director = new Director(dname, represent, rate, imgurl);
			directorList.add(director);
		}
    	return directorList;
    }
	
	public List<ScriptWriter> getSriptWriterList(){
		String regexblock = "<h4>编剧 Writer</h4>(.*?)</div>";
		Pattern pattern = Pattern.compile(regexblock);
		Matcher matcher = pattern.matcher(this.mtimeActorContent);
		String block = "";
		if(matcher.find()){
			block = matcher.group(1);
		}
		String regexDirectorList = "<a target=\"_blank\" href=\"(.*?)\">(.*?)</a>";
		pattern = Pattern.compile(regexDirectorList);
		matcher = pattern.matcher(block);
		List<ScriptWriter> scriptWriterList = new ArrayList<ScriptWriter>();
		while(matcher.find()){
			String writerurl = matcher.group(1);
			String wname = matcher.group(2);
			People people = this.getPeople(writerurl);
			List<String> represent = people.represent;
			ScriptWriter scriptwriter = new ScriptWriter(wname, represent);
			scriptWriterList.add(scriptwriter);
		}
    	return scriptWriterList;
	}
	public  People  getPeople(String url){
		String regexId = "http://people.mtime.com/(.*?)/";
	    Pattern pattern = Pattern.compile(regexId);
	    Matcher matcher = pattern.matcher(url);
	    String pid = "";
	    if(matcher.find()){
	    	pid = matcher.group(1);
	    }
		String peopleUrl = "http://people.mtime.com/"+pid+"/filmographies/";
		String peopleContent = getContent(peopleUrl, "utf-8");
		String regexMovie = "<a href=\"http://movie.mtime.com/.*?\" target=\"_blank\">(.*?)</a>";
		pattern = Pattern.compile(regexMovie);
		matcher = pattern.matcher(peopleContent);
		List<String> represent = new ArrayList<String>();
		while(matcher.find()){
			represent.add(matcher.group(1));
		}
	    String ratingUrl = "http://service.library.mtime.com/Person.api?Ajax_CallBack=true&Ajax_CallBackType=Mtime.Library.Services&Ajax_CallBackMethod=GetPersonRatingInfo&Ajax_CrossDomain=1&Ajax_RequestUrl=http://people.mtime.com/"+pid+"/&Ajax_CallBackArgument0="+pid;
	    String ratingContent = getContent(ratingUrl, "utf-8");
	    String regexRating = "finalRating\":(.*?),\"ratingCount";
	    pattern = Pattern.compile(regexRating);
	    matcher = pattern.matcher(ratingContent);
	    String rate= "";
	    if(matcher.find()){
	    	rate = matcher.group(1);
	    }
	    People people = new People(pid, represent, rate);
	    return people;
	}
	
	
    public Company getCompany(String companyUrl, String cname){
    	List<String>  futureRepresent = new ArrayList<String>();
    	List<String>  pastRepresent = new ArrayList<String>();
    	String representContent = getContent(companyUrl, "utf-8");
    	String regexRepresent = "<div class=\".*?\" movieid=\".*?\" year=\"(.*?)\" month=\".*?\" day=\".*?\" >	.*?<a href=\".*?\" class=\"img\" target=\"_blank\" title=\"(.*?)\">";
    	Pattern pattern = Pattern.compile(regexRepresent);
		Matcher matcher = pattern.matcher(representContent);
		while(matcher.find()){
			int year = Integer.parseInt(matcher.group(1));
			String represent = matcher.group(2);
			if(year > 2016){
				futureRepresent.add(represent);
			}
			else{
				pastRepresent.add(represent);
			}
		}
		 Company company = new Company(cname,  futureRepresent,  pastRepresent,  representContent);
		 return company;
    }
    
    public List<Role> getRoleList(){
    	String regexRole = "<li class=\"roleIntroduction-item \">(.*?)</li>";
    	Pattern pattern = Pattern.compile(regexRole);
    	Matcher matcher = pattern.matcher(this.content);
    	List<Role> roleList = new ArrayList<Role>();
    	while(matcher.find()){
    		String block = matcher.group(1);
    		String  regexImg = "<img src=\"(.*?)\" /></div>";
    		Pattern pattern_2 = Pattern.compile(regexImg);
    		Matcher matcher_2 = pattern.matcher(block);
    		String imgurl = "";
    		if(matcher_2.find()){
    			imgurl = matcher_2.group(1);
    		}
    		String regexRoleName = "<div class=\"role-name\"><span class=\"item-value\">(.*?)</span>";
    		pattern_2 = Pattern.compile(regexRoleName);
    		matcher_2 = pattern_2.matcher(block);
    		String roleName = "";
    		if(matcher_2.find()){
    			roleName = matcher_2.group(1);
    			roleName = roleName.replaceAll("<.*?>", "");
    		}
    		String regexIntro = "<dd class=\"role-description\">(.*?)</dd>";
    		pattern_2 =  Pattern.compile(regexIntro);
    		matcher_2 = pattern_2.matcher(block);
    		String introduction = "";
    		if(matcher_2.find()){
    			introduction = matcher_2.group(1);
    		}
    		String regexActor = "<a target=_blank href=\"/view/(.*?)\">(.*?)</a>";
    		pattern_2 = Pattern.compile(regexActor);
    		matcher_2 = pattern_2.matcher(block);
    		String aid = "";
    		String actorName = "";
    		Actor actor = null;
    		if(matcher_2.find()){
    			aid = matcher_2.group(1);
    			System.out.println(aid);
    			actor = this.getActor(aid);
    		}
    		 Role role = new Role(roleName,  introduction,actor, imgurl);
    		 roleList.add(role);
    	}
    	return roleList;
    }
    
    public Actor getActor(String aid){
    	String actUrl = "http://baike.baidu.com/view/"+ aid;
    	String actContent = getContent(actUrl, "utf-8");
    	String regexName = "<title>(.*?)_百度百科</title>";
    	Pattern pattern = Pattern.compile(regexName);
    	Matcher matcher = pattern.matcher(actContent);
    	String actorname = "";
    	if(matcher.find()){
    		actorname = matcher.group(1);
    	}
    	String regexCountry = "<dt class=\"basicInfo-item name\">国&nbsp;&nbsp;&nbsp;&nbsp;籍</dt><dd class=\"basicInfo-item value\">(.*?)</dd>";
    	pattern = Pattern.compile(regexCountry);
    	matcher = pattern.matcher(actContent);
    	String country = "";
    	if(matcher.find()){
    		country = matcher.group(1); 
    	}
    	String regexBirthday = "dt class=\"basicInfo-item name\">出生日期</dt><dd class=\"basicInfo-item value\">(.*?)</dd>";
    	pattern = Pattern.compile(regexBirthday);
    	matcher = pattern.matcher(actContent);
    	String birthday = "";
    	if(matcher.find()){
    		birthday = matcher.group(1);
    	}
    	String regexRepresent = "<dt class=\"basicInfo-item name\">代表作品</dt><dd class=\"basicInfo-item value\">(.*?)</dd>";
    	pattern = Pattern.compile(regexRepresent);
    	matcher = pattern.matcher(actContent);
    	String represent = "";
    	if(matcher.find()){
    		represent = matcher.group(1);
    	}
    	represent = represent.replaceAll("<.*?>", "");
    	String[] tokens = represent.split("、"); 
    	List<String> representList = new ArrayList<String>();
    	for(String rp : tokens){
    		representList.add(rp);
    	}
    	String regexImg = "<div class=\"feature_poster\">.*?<img src=\"(.*?)\" /><button class=\"picAlbumBtn\"><em></em><span>图集</span></button>";
    	pattern = Pattern.compile(regexImg);
    	matcher = pattern.matcher(actContent);
    	String imgurl = "";
    	if(matcher.find()){
    		imgurl = matcher.group(1);
    	}
    	Actor actor = new Actor(actorname, birthday, country, imgurl, representList);
    	return actor;
    }
    
}

	
class Introduction{

	String score; 			//豆瓣评分
	String boxOffice;			//票房成绩
	String release_time;		//上映时间
	String area;						//国家地区
	String length;					//片长
	List<String> keywords;//关键词列表
	String imgUrl;					//图片链接
	
	public Introduction(String score, String boxOffice, String release_time, String area, String length, List<String> keywords, String imgUrl){
		this.score = score;
		this.boxOffice = boxOffice;
		this.release_time = release_time;
		this.area = area;
		this.length = length;
		this.keywords = keywords;
		this.imgUrl = imgUrl;
	}
	
}

class Information{
	String story = null; //剧情解析
	String comment = null;  //影片评论
	String doubanUrl = null; //豆瓣地址
	List<String> prize = null; //获得奖项
	
	public Information(String  story, String comment, String doubanUrl, List<String> prize ){
		this.story = story;
		this.comment = comment;
		this.doubanUrl = doubanUrl;
		this.prize = prize;
	}
}



 class MainTheme{
	 	String style; 	// 风格
	 	String  theme; 	//主题
	 	List<String> similarMovie;	//相关电影
	 	public MainTheme(String style, String theme, List<String> similarMovie){
	 		this.style = style;
	 		this.theme = theme;
	 		this.similarMovie  = similarMovie;
	 	}
 }
 
class Manufacture{
	
	List<Company> company ;
	List<Director> directors;
	List<ScriptWriter> scriptwriters;
	
	public Manufacture(List<Company> company, List<Director> directors, List<ScriptWriter> scriptwriters){
		this.company = company;
		this.directors = directors;
		this.scriptwriters = scriptwriters;
	}
}

class Company{
	String cname = null; //公司名字
	List<String> futureRepresent = null; //未来作品
	List<String> pastRepresent = null;//过去作品
	String representContent = null;
	public Company(String cname, List<String> futureRepresent, List<String> pastRepresent, String representContent){
		this.cname = cname;
		this.futureRepresent = futureRepresent;
		this.pastRepresent = pastRepresent;
		this.representContent = representContent;
	}
}
	
class  Director{
	String dname= null;//导演名称
	List<String> represent = null;//代表作品
	String rate = null; //导演评分
	String imgurl = null;//图片地址
	public Director(String dname,  List<String> represent , String rate, String imgurl){
		this.dname = dname;
		this.represent = represent;
		this.imgurl = imgurl;
		this.rate = rate;
	}
}
	
class ScriptWriter{
	String sname = null;//编剧名称
	List<String> represent = null;//代表作品
	public ScriptWriter(String sname,  List<String> represent){
		this.sname = sname;
		this.represent = represent;
	}
	
}

class People{
	String id; 		//mtime网上的id号
	List<String> represent ;			//代表作品
	String rate;			//评分
	public People(String id , List<String> represent, String rate){
		this.id = id ;
		this.represent = represent;
		this.rate = rate;
	}
}

 class Role{
	 String name; //角色名称
	 String introduction; //角色介绍
	 String imgurl; //角色图片
	 Actor actor; //演员
	 
	 public Role(String name, String introduction, Actor actor, String imgurl){
		 this.name = name;
		 this.introduction = introduction;
		 this.actor  = actor;
		 this.imgurl = imgurl;
		 
	 }
	 
 }
 
 class Actor{
	 String name;  //演员名字
	 String birthday;  //演员出生日期
	 String country; //国籍
	 String imgurl;  //图片
	 List<String> represent; //代表作品
	 public Actor(String name,String birthday, String country, String imgurl , List<String> represent){
		 this.name = name;
		 this.birthday = birthday;
		 this.country = country;
		 this.imgurl = imgurl;
		 this.represent = represent;
	 }
 }


