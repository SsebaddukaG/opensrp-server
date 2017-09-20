package org.opensrp.service;

import org.opensrp.domain.Client;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.MultimediaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class MultimediaService {

	private static Logger logger = LoggerFactory.getLogger(MultimediaService.class.toString());

	public static final String IMAGES_DIR = "patient_images";

	private static final String VIDEOS_DIR = "videos";

	private final MultimediaRepository multimediaRepository;

	private final ClientService clientService;

	private String multimediaDirPath;

	@Value("#{opensrp['multimedia.directory.name']}")
	String baseMultimediaDirPath;

	@Autowired
	public MultimediaService(MultimediaRepository multimediaRepository, ClientService clientService) {
		this.multimediaRepository = multimediaRepository;
		this.clientService = clientService;
	}

	public String saveMultimediaFile(MultimediaDTO multimediaDTO, MultipartFile file) {

		boolean uploadStatus = uploadFile(multimediaDTO, file);

		String[] multimediaDirPathSplit = multimediaDirPath.split("/", 3);
		String multimediaDirPathDB = File.separator + multimediaDirPathSplit[2];

		if (uploadStatus) {
			try {
				logger.info("Image path : " + multimediaDirPath);

				Multimedia multimediaFile = new Multimedia().withCaseId(multimediaDTO.getCaseId())
						.withProviderId(multimediaDTO.getProviderId()).withContentType(multimediaDTO.getContentType())
						.withFilePath(multimediaDTO.getFilePath()).withFileCategory(multimediaDTO.getFileCategory());

				multimediaRepository.add(multimediaFile);
				Client client = clientService.getByBaseEntityId(multimediaDTO.getCaseId());
				if (client != null) {
					if (client.getAttribute("Patient Image") != null) {
						client.removeAttribute("Patient Image");
					}
					client.addAttribute("Patient Image", multimediaDTO.getCaseId() + ".jpg");
					client.setServerVersion(null);
					clientService.updateClient(client);
				}
				return "success";

			}
			catch (Exception e) {
				e.getMessage();
			}
		}

		return "fail";
	}

	public boolean uploadFile(MultimediaDTO multimediaDTO, MultipartFile multimediaFile) {

		if (!multimediaFile.isEmpty()) {
			try {

				multimediaDirPath = baseMultimediaDirPath + File.separator;
				String fileExt = ".jpg";
				switch (multimediaDTO.getContentType()) {

					case "application/octet-stream":
						multimediaDirPath += VIDEOS_DIR;
						fileExt = ".mp4";
						break;

					case "image/jpeg":
						multimediaDirPath += IMAGES_DIR;
						fileExt = ".jpg";
						break;

					case "image/gif":
						multimediaDirPath += IMAGES_DIR;
						fileExt = ".gif";
						break;

					case "image/png":
						multimediaDirPath += IMAGES_DIR;
						fileExt = ".png";
						break;

					default:
						throw new IllegalArgumentException("Unknown content type : " + multimediaDTO.getContentType());
				}
				new File(multimediaDirPath).mkdirs();

				String fileName = multimediaDirPath + File.separator + multimediaDTO.getCaseId() + fileExt;
				multimediaDTO.withFilePath(fileName);
				File multimediaDir = new File(fileName);

				multimediaFile.transferTo(multimediaDir);

			/*
			 byte[] bytes = multimediaFile.getBytes();
			 	
			 BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(multimediaDirPath));
				stream.write(bytes);
				stream.close();*/

				return true;

			}
			catch (Exception e) {
				logger.error("", e);
				return false;
			}
		} else {
			return false;
		}
	}

	private void makeMultimediaDir(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists())
			file.mkdirs();
	}

	public List<Multimedia> getMultimediaFiles(String providerId) {
		return multimediaRepository.all(providerId);
	}

	public Multimedia findByCaseId(String entityId) {
		return multimediaRepository.findByCaseId(entityId);
	}
}
